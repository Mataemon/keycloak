package org.keycloak.federation.ldap;

import org.jboss.logging.Logger;
import org.keycloak.models.UserFederationProvider;
import org.keycloak.models.UserFederationProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.SocialLinkModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.picketlink.idm.IdentityManagementException;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LDAPFederationProvider implements UserFederationProvider {
    private static final Logger logger = Logger.getLogger(LDAPFederationProvider.class);
    public static final String LDAP_ID = "LDAP_ID";

    protected KeycloakSession session;
    protected UserFederationProviderModel model;
    protected PartitionManager partitionManager;

    protected static final Set<String> supportedCredentialTypes = new HashSet<String>();

    static
    {
        supportedCredentialTypes.add(UserCredentialModel.PASSWORD);
    }

    public LDAPFederationProvider(KeycloakSession session, UserFederationProviderModel model, PartitionManager partitionManager) {
        this.session = session;
        this.model = model;
        this.partitionManager = partitionManager;
    }

    private ModelException convertIDMException(IdentityManagementException ie) {
        Throwable realCause = ie;
        while (realCause.getCause() != null) {
            realCause = realCause.getCause();
        }

        // Use the message from the realCause
        return new ModelException(realCause.getMessage(), ie);
    }

    public KeycloakSession getSession() {
        return session;
    }

    public UserFederationProviderModel getModel() {
        return model;
    }

    public PartitionManager getPartitionManager() {
        return partitionManager;
    }

    @Override
    public UserModel proxy(UserModel local) {
        // todo
        return new LDAPUserModelDelegate(local, this);
    }

    @Override
    public Set<String> getSupportedCredentialTypes() {
        return supportedCredentialTypes;
    }

    @Override
    public boolean isRegistrationSupported() {
        return true;
    }

    @Override
    public UserModel register(RealmModel realm, UserModel user) {
        IdentityManager identityManager = getIdentityManager();

        try {
            User picketlinkUser = new User(user.getUsername());
            picketlinkUser.setFirstName(user.getFirstName());
            picketlinkUser.setLastName(user.getLastName());
            picketlinkUser.setEmail(user.getEmail());
            identityManager.add(picketlinkUser);
            return proxy(user);
        } catch (IdentityManagementException ie) {
            throw convertIDMException(ie);
        }

    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        IdentityManager identityManager = getIdentityManager();

        try {
            User picketlinkUser = BasicModel.getUser(identityManager, user.getUsername());
            if (picketlinkUser == null) {
                return false;
            }
            identityManager.remove(picketlinkUser);
            return true;
        } catch (IdentityManagementException ie) {
            throw convertIDMException(ie);
        }
    }

    @Override
    public List<UserModel> searchByAttributes(Map<String, String> attributes, RealmModel realm) {
        IdentityManager identityManager = getIdentityManager();
        List<UserModel> searchResults =new LinkedList<UserModel>();
        try {
            Map<String, User> results = new HashMap<String, User>();
            if (attributes.containsKey(USERNAME)) {
                User user = BasicModel.getUser(identityManager, attributes.get(USERNAME));
                if (user != null) results.put(user.getLoginName(), user);
            } else if (attributes.containsKey(EMAIL)) {
                User user = queryByEmail(identityManager, attributes.get(EMAIL));
                if (user != null) results.put(user.getLoginName(), user);
            } else if (attributes.containsKey(FIRST_NAME) || attributes.containsKey(LAST_NAME)) {
                IdentityQuery<User> query = identityManager.createIdentityQuery(User.class);
                if (attributes.containsKey(FIRST_NAME)) {
                    query.setParameter(User.FIRST_NAME, attributes.get(FIRST_NAME));
                }
                if (attributes.containsKey(LAST_NAME)) {
                    query.setParameter(User.LAST_NAME, attributes.get(LAST_NAME));
                }
                List<User> agents = query.getResultList();
                for (User user : agents) {
                    results.put(user.getLoginName(), user);
                }
            }
            for (User user : results.values()) {
                if (session.userStorage().getUserByUsername(user.getLoginName(), realm) == null) {
                    UserModel imported = importUserFromPicketlink(realm, user);
                    searchResults.add(imported);
                }
            }
        } catch (IdentityManagementException ie) {
            throw convertIDMException(ie);
        }
        return searchResults;
    }

    @Override
    public boolean isValid(UserModel local) {
        IdentityManager identityManager = getIdentityManager();

        try {
            User picketlinkUser = BasicModel.getUser(identityManager, local.getUsername());
            if (picketlinkUser == null) {
                return false;
            }
            return picketlinkUser.getId().equals(local.getAttribute(LDAP_ID));
        } catch (IdentityManagementException ie) {
            throw convertIDMException(ie);
        }
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        IdentityManager identityManager = getIdentityManager();

        try {
            User picketlinkUser = BasicModel.getUser(identityManager, username);
            if (picketlinkUser == null) {
                return null;
            }

            return importUserFromPicketlink(realm, picketlinkUser);
        } catch (IdentityManagementException ie) {
            throw convertIDMException(ie);
        }
    }

    public IdentityManager getIdentityManager() {
        return partitionManager.createIdentityManager();
    }

    protected UserModel importUserFromPicketlink(RealmModel realm, User picketlinkUser) {
        String email = (picketlinkUser.getEmail() != null && picketlinkUser.getEmail().trim().length() > 0) ? picketlinkUser.getEmail() : null;
        UserModel imported = session.userStorage().addUser(realm, picketlinkUser.getLoginName());
        imported.setEnabled(true);
        imported.setEmail(email);
        imported.setFirstName(picketlinkUser.getFirstName());
        imported.setLastName(picketlinkUser.getLastName());
        imported.setFederationLink(model.getId());
        imported.setAttribute(LDAP_ID, picketlinkUser.getId());
        return proxy(imported);
    }

    protected User queryByEmail(IdentityManager identityManager, String email) throws IdentityManagementException {
        List<User> agents = identityManager.createIdentityQuery(User.class)
                .setParameter(User.EMAIL, email).getResultList();

        if (agents.isEmpty()) {
            return null;
        } else if (agents.size() == 1) {
            return agents.get(0);
        } else {
            throw new IdentityManagementException("Error - multiple Agent objects found with same login name");
        }
    }


    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        IdentityManager identityManager = getIdentityManager();

        try {
            User picketlinkUser = queryByEmail(identityManager, email);
            if (picketlinkUser == null) {
                return null;
            }
            return importUserFromPicketlink(realm, picketlinkUser);
        } catch (IdentityManagementException ie) {
            throw convertIDMException(ie);
        }
    }

    @Override
    public void preRemove(RealmModel realm) {
        // complete Don't think we have to do anything
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        // complete I don't think we have to do anything here
    }

    public boolean validPassword(String username, String password) {
        IdentityManager identityManager = getIdentityManager();

        try {
            UsernamePasswordCredentials credential = new UsernamePasswordCredentials();
            credential.setUsername(username);
            credential.setPassword(new Password(password.toCharArray()));
            identityManager.validateCredentials(credential);
            if (credential.getStatus() == Credentials.Status.VALID) {
                return true;
            } else {
                return false;
            }
        } catch (IdentityManagementException ie) {
            throw convertIDMException(ie);
        }
    }


    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, List<UserCredentialModel> input) {
        for (UserCredentialModel cred : input) {
            if (cred.getType().equals(UserCredentialModel.PASSWORD)) {
                return validPassword(user.getUsername(), cred.getValue());
            } else {
                return false; // invalid cred type
            }
        }
        return true;
    }

    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, UserCredentialModel... input) {
        for (UserCredentialModel cred : input) {
            if (cred.getType().equals(UserCredentialModel.PASSWORD)) {
                return validPassword(user.getUsername(), cred.getValue());
            } else {
                return false; // invalid cred type
            }
        }
        return true;
    }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
