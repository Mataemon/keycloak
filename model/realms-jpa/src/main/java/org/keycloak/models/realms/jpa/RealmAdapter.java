package org.keycloak.models.realms.jpa;

import org.keycloak.models.AuthenticationProviderModel;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.RequiredCredentialModel;
import org.keycloak.models.realms.Application;
import org.keycloak.models.realms.Client;
import org.keycloak.models.realms.OAuthClient;
import org.keycloak.models.realms.Realm;
import org.keycloak.models.realms.RealmProvider;
import org.keycloak.models.realms.Role;
import org.keycloak.models.realms.jpa.entities.ApplicationEntity;
import org.keycloak.models.realms.jpa.entities.AuthenticationProviderEntity;
import org.keycloak.models.realms.jpa.entities.OAuthClientEntity;
import org.keycloak.models.realms.jpa.entities.RealmEntity;
import org.keycloak.models.realms.jpa.entities.RequiredCredentialEntity;
import org.keycloak.models.realms.jpa.entities.RoleEntity;
import org.keycloak.models.realms.jpa.entities.ScopeMappingEntity;
import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RealmAdapter implements Realm {
    protected RealmEntity realm;
    protected RealmProvider provider;
    protected EntityManager em;
    protected volatile transient PublicKey publicKey;
    protected volatile transient PrivateKey privateKey;
    protected PasswordPolicy passwordPolicy;

    public RealmAdapter(RealmProvider provider, EntityManager em, RealmEntity realm) {
        this.provider = provider;
        this.em = em;
        this.realm = realm;
    }

    public RealmEntity getEntity() {
        return realm;
    }

    @Override
    public String getId() {
        return realm.getId();
    }

    @Override
    public String getName() {
        return realm.getName();
    }

    @Override
    public void setName(String name) {
        realm.setName(name);
        em.flush();
    }

    @Override
    public boolean isEnabled() {
        return realm.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        realm.setEnabled(enabled);
        em.flush();
    }

    @Override
    public boolean isSslNotRequired() {
        return realm.isSslNotRequired();
    }

    @Override
    public void setSslNotRequired(boolean sslNotRequired) {
        realm.setSslNotRequired(sslNotRequired);
        em.flush();
    }

    @Override
    public boolean isPasswordCredentialGrantAllowed() {
        return realm.isPasswordCredentialGrantAllowed();
    }

    @Override
    public void setPasswordCredentialGrantAllowed(boolean passwordCredentialGrantAllowed) {
        realm.setPasswordCredentialGrantAllowed(passwordCredentialGrantAllowed);
        em.flush();
    }

    @Override
    public boolean isRegistrationAllowed() {
        return realm.isRegistrationAllowed();
    }

    @Override
    public void setRegistrationAllowed(boolean registrationAllowed) {
        realm.setRegistrationAllowed(registrationAllowed);
        em.flush();
    }

    @Override
    public boolean isRememberMe() {
        return realm.isRememberMe();
    }

    @Override
    public void setRememberMe(boolean rememberMe) {
        realm.setRememberMe(rememberMe);
        em.flush();
    }

    @Override
    public boolean isBruteForceProtected() {
        return realm.isBruteForceProtected();
    }

    @Override
    public void setBruteForceProtected(boolean value) {
        realm.setBruteForceProtected(value);
    }

    @Override
    public int getMaxFailureWaitSeconds() {
        return realm.getMaxFailureWaitSeconds();
    }

    @Override
    public void setMaxFailureWaitSeconds(int val) {
        realm.setMaxFailureWaitSeconds(val);
    }

    @Override
    public int getWaitIncrementSeconds() {
        return realm.getWaitIncrementSeconds();
    }

    @Override
    public void setWaitIncrementSeconds(int val) {
        realm.setWaitIncrementSeconds(val);
    }

    @Override
    public long getQuickLoginCheckMilliSeconds() {
        return realm.getQuickLoginCheckMilliSeconds();
    }

    @Override
    public void setQuickLoginCheckMilliSeconds(long val) {
        realm.setQuickLoginCheckMilliSeconds(val);
    }

    @Override
    public int getMinimumQuickLoginWaitSeconds() {
        return realm.getMinimumQuickLoginWaitSeconds();
    }

    @Override
    public void setMinimumQuickLoginWaitSeconds(int val) {
        realm.setMinimumQuickLoginWaitSeconds(val);
    }

    @Override
    public int getMaxDeltaTimeSeconds() {
        return realm.getMaxDeltaTimeSeconds();
    }

    @Override
    public void setMaxDeltaTimeSeconds(int val) {
        realm.setMaxDeltaTimeSeconds(val);
    }

    @Override
    public int getFailureFactor() {
        return realm.getFailureFactor();
    }

    @Override
    public void setFailureFactor(int failureFactor) {
        realm.setFailureFactor(failureFactor);
    }

    @Override
    public boolean isVerifyEmail() {
        return realm.isVerifyEmail();
    }

    @Override
    public void setVerifyEmail(boolean verifyEmail) {
        realm.setVerifyEmail(verifyEmail);
        em.flush();
    }

    @Override
    public boolean isResetPasswordAllowed() {
        return realm.isResetPasswordAllowed();
    }

    @Override
    public void setResetPasswordAllowed(boolean resetPasswordAllowed) {
        realm.setResetPasswordAllowed(resetPasswordAllowed);
        em.flush();
    }

    @Override
    public int getNotBefore() {
        return realm.getNotBefore();
    }

    @Override
    public void setNotBefore(int notBefore) {
        realm.setNotBefore(notBefore);
    }

    @Override
    public int getAccessTokenLifespan() {
        return realm.getAccessTokenLifespan();
    }

    @Override
    public void setAccessTokenLifespan(int tokenLifespan) {
        realm.setAccessTokenLifespan(tokenLifespan);
        em.flush();
    }

    @Override
    public int getSsoSessionIdleTimeout() {
        return realm.getSsoSessionIdleTimeout();
    }

    @Override
    public void setSsoSessionIdleTimeout(int seconds) {
        realm.setSsoSessionIdleTimeout(seconds);
    }

    @Override
    public int getSsoSessionMaxLifespan() {
        return realm.getSsoSessionMaxLifespan();
    }

    @Override
    public void setSsoSessionMaxLifespan(int seconds) {
        realm.setSsoSessionMaxLifespan(seconds);
    }

    @Override
    public int getAccessCodeLifespan() {
        return realm.getAccessCodeLifespan();
    }

    @Override
    public void setAccessCodeLifespan(int accessCodeLifespan) {
        realm.setAccessCodeLifespan(accessCodeLifespan);
        em.flush();
    }

    @Override
    public int getAccessCodeLifespanUserAction() {
        return realm.getAccessCodeLifespanUserAction();
    }

    @Override
    public void setAccessCodeLifespanUserAction(int accessCodeLifespanUserAction) {
        realm.setAccessCodeLifespanUserAction(accessCodeLifespanUserAction);
        em.flush();
    }

    @Override
    public String getPublicKeyPem() {
        return realm.getPublicKeyPem();
    }

    @Override
    public void setPublicKeyPem(String publicKeyPem) {
        realm.setPublicKeyPem(publicKeyPem);
        em.flush();
    }

    @Override
    public String getPrivateKeyPem() {
        return realm.getPrivateKeyPem();
    }

    @Override
    public void setPrivateKeyPem(String privateKeyPem) {
        realm.setPrivateKeyPem(privateKeyPem);
        em.flush();
    }

    @Override
    public PublicKey getPublicKey() {
        if (publicKey != null) return publicKey;
        publicKey = KeycloakModelUtils.getPublicKey(getPublicKeyPem());
        return publicKey;
    }

    @Override
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        String publicKeyPem = KeycloakModelUtils.getPemFromKey(publicKey);
        setPublicKeyPem(publicKeyPem);
    }

    @Override
    public PrivateKey getPrivateKey() {
        if (privateKey != null) return privateKey;
        privateKey = KeycloakModelUtils.getPrivateKey(getPrivateKeyPem());
        return privateKey;
    }

    @Override
    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
        String privateKeyPem = KeycloakModelUtils.getPemFromKey(privateKey);
        setPrivateKeyPem(privateKeyPem);
    }

    protected RequiredCredentialModel initRequiredCredentialModel(String type) {
        RequiredCredentialModel model = RequiredCredentialModel.BUILT_IN.get(type);
        if (model == null) {
            throw new RuntimeException("Unknown credential type " + type);
        }
        return model;
    }

    @Override
    public void addRequiredCredential(String type) {
        RequiredCredentialModel model = initRequiredCredentialModel(type);
        addRequiredCredential(model);
        em.flush();
    }

    public void addRequiredCredential(RequiredCredentialModel model) {
        RequiredCredentialEntity entity = new RequiredCredentialEntity();
        entity.setInput(model.isInput());
        entity.setSecret(model.isSecret());
        entity.setType(model.getType());
        entity.setFormLabel(model.getFormLabel());
        em.persist(entity);
        realm.getRequiredCredentials().add(entity);
        em.flush();
    }

    @Override
    public void updateRequiredCredentials(Set<String> creds) {
        Collection<RequiredCredentialEntity> relationships = realm.getRequiredCredentials();
        if (relationships == null) relationships = new ArrayList<RequiredCredentialEntity>();

        Set<String> already = new HashSet<String>();
        List<RequiredCredentialEntity> remove = new ArrayList<RequiredCredentialEntity>();
        for (RequiredCredentialEntity rel : relationships) {
            if (!creds.contains(rel.getType())) {
                remove.add(rel);
            } else {
                already.add(rel.getType());
            }
        }
        for (RequiredCredentialEntity entity : remove) {
            relationships.remove(entity);
            em.remove(entity);
        }
        for (String cred : creds) {
            if (!already.contains(cred)) {
                addRequiredCredential(cred);
            }
        }
        em.flush();
    }


    @Override
    public List<RequiredCredentialModel> getRequiredCredentials() {
        List<RequiredCredentialModel> requiredCredentialModels = new ArrayList<RequiredCredentialModel>();
        Collection<RequiredCredentialEntity> entities = realm.getRequiredCredentials();
        if (entities == null) return requiredCredentialModels;
        for (RequiredCredentialEntity entity : entities) {
            RequiredCredentialModel model = new RequiredCredentialModel();
            model.setFormLabel(entity.getFormLabel());
            model.setType(entity.getType());
            model.setSecret(entity.isSecret());
            model.setInput(entity.isInput());
            requiredCredentialModels.add(model);
        }
        return requiredCredentialModels;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getDefaultRoles() {
        Collection<RoleEntity> entities = realm.getDefaultRoles();
        List<String> roles = new ArrayList<String>();
        if (entities == null) return roles;
        for (RoleEntity entity : entities) {
            roles.add(entity.getName());
        }
        return roles;
    }

    @Override
    public void addDefaultRole(String name) {
        Role role = getRole(name);
        Collection<RoleEntity> entities = realm.getDefaultRoles();
        for (RoleEntity entity : entities) {
            if (entity.getId().equals(role.getId())) {
                return;
            }
        }
        realm.getDefaultRoles().add(((RoleAdapter) role).getRole());
        em.flush();
    }

    public static boolean contains(String str, String[] array) {
        for (String s : array) {
            if (str.equals(s)) return true;
        }
        return false;
    }

    @Override
    public void updateDefaultRoles(String[] defaultRoles) {
        Collection<RoleEntity> entities = realm.getDefaultRoles();
        Set<String> already = new HashSet<String>();
        List<RoleEntity> remove = new ArrayList<RoleEntity>();
        for (RoleEntity rel : entities) {
            if (!contains(rel.getName(), defaultRoles)) {
                remove.add(rel);
            } else {
                already.add(rel.getName());
            }
        }
        for (RoleEntity entity : remove) {
            entities.remove(entity);
        }
        em.flush();
        for (String roleName : defaultRoles) {
            if (!already.contains(roleName)) {
                addDefaultRole(roleName);
            }
        }
        em.flush();
    }

    @Override
    public Client findClient(String clientId) {
        Client model = getApplicationByName(clientId);
        if (model != null) return model;
        return getOAuthClient(clientId);
    }

    @Override
    public Map<String, Application> getApplicationNameMap() {
        Map<String, Application> map = new HashMap<String, Application>();
        for (Application app : getApplications()) {
            map.put(app.getName(), app);
        }
        return map;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Application> getApplications() {
        List<Application> list = new ArrayList<Application>();
        if (realm.getApplications() == null) return list;
        for (ApplicationEntity entity : realm.getApplications()) {
            list.add(new ApplicationAdapter(provider, em, entity));
        }
        return list;
    }

    @Override
    public Application addApplication(String id, String name) {
        ApplicationEntity applicationData = new ApplicationEntity();
        applicationData.setId(id);
        applicationData.setName(name);
        applicationData.setEnabled(true);
        applicationData.setRealm(realm);
        realm.getApplications().add(applicationData);
        em.persist(applicationData);
        em.flush();
        Application resource = new ApplicationAdapter(provider, em, applicationData);
        em.flush();
        return resource;
    }

    @Override
    public boolean removeApplication(Application application) {
        for (Role role : application.getRoles()) {
            application.removeRole(role);
        }

        ApplicationEntity applicationEntity = null;
        Iterator<ApplicationEntity> it = realm.getApplications().iterator();
        while (it.hasNext()) {
            ApplicationEntity ae = it.next();
            if (ae.getId().equals(application.getId())) {
                applicationEntity = ae;
                it.remove();
                break;
            }
        }
        for (ApplicationEntity a : realm.getApplications()) {
            if (a.getId().equals(application.getId())) {
                applicationEntity = a;
            }
        }
        if (application == null) {
            return false;
        }
        em.remove(applicationEntity);
        em.createQuery("delete from " + ScopeMappingEntity.class.getSimpleName() + " where client = :client").setParameter("client", applicationEntity).executeUpdate();

        return true;
    }

    @Override
    public Application getApplicationByName(String name) {
        return getApplicationNameMap().get(name);
    }

    @Override
    public boolean isSocial() {
        return realm.isSocial();
    }

    @Override
    public void setSocial(boolean social) {
        realm.setSocial(social);
        em.flush();
    }

    @Override
    public boolean isUpdateProfileOnInitialSocialLogin() {
        return realm.isUpdateProfileOnInitialSocialLogin();
    }

    @Override
    public void setUpdateProfileOnInitialSocialLogin(boolean updateProfileOnInitialSocialLogin) {
        realm.setUpdateProfileOnInitialSocialLogin(updateProfileOnInitialSocialLogin);
        em.flush();
    }

    @Override
    public OAuthClient addOAuthClient(String id, String name) {
        OAuthClientEntity data = new OAuthClientEntity();
        data.setId(id);
        data.setEnabled(true);
        data.setName(name);
        data.setRealm(realm);
        em.persist(data);
        em.flush();
        return new OAuthClientAdapter(provider, data, em);
    }

    @Override
    public boolean removeOAuthClient(OAuthClient client) {
        em.createQuery("delete from " + ScopeMappingEntity.class.getSimpleName() + " where client = :client").setParameter("client", client).executeUpdate();
        em.remove(client);
        return true;
    }


    @Override
    public OAuthClient getOAuthClient(String name) {
        TypedQuery<OAuthClientEntity> query = em.createNamedQuery("findOAuthClientByName", OAuthClientEntity.class);
        query.setParameter("name", name);
        query.setParameter("realm", realm);
        List<OAuthClientEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new OAuthClientAdapter(provider, entities.get(0), em);
    }

    @Override
    public List<OAuthClient> getOAuthClients() {
        TypedQuery<OAuthClientEntity> query = em.createNamedQuery("findOAuthClientByRealm", OAuthClientEntity.class);
        query.setParameter("realm", realm);
        List<OAuthClientEntity> entities = query.getResultList();
        List<OAuthClient> list = new ArrayList<OAuthClient>();
        for (OAuthClientEntity entity : entities) list.add(new OAuthClientAdapter(provider, entity, em));
        return list;
    }

    @Override
    public Map<String, String> getSmtpConfig() {
        return realm.getSmtpConfig();
    }

    @Override
    public void setSmtpConfig(Map<String, String> smtpConfig) {
        realm.setSmtpConfig(smtpConfig);
        em.flush();
    }

    @Override
    public Map<String, String> getSocialConfig() {
        return realm.getSocialConfig();
    }

    @Override
    public void setSocialConfig(Map<String, String> socialConfig) {
        realm.setSocialConfig(socialConfig);
        em.flush();
    }

    @Override
    public Map<String, String> getLdapServerConfig() {
        return realm.getLdapServerConfig();
    }

    @Override
    public void setLdapServerConfig(Map<String, String> ldapServerConfig) {
        realm.setLdapServerConfig(ldapServerConfig);
        em.flush();
    }

    @Override
    public List<AuthenticationProviderModel> getAuthenticationProviders() {
        List<AuthenticationProviderEntity> entities = realm.getAuthenticationProviders();
        List<AuthenticationProviderEntity> copy = new ArrayList<AuthenticationProviderEntity>();
        for (AuthenticationProviderEntity entity : entities) {
            copy.add(entity);

        }
        Collections.sort(copy, new Comparator<AuthenticationProviderEntity>() {

            @Override
            public int compare(AuthenticationProviderEntity o1, AuthenticationProviderEntity o2) {
                return o1.getPriority() - o2.getPriority();
            }

        });
        List<AuthenticationProviderModel> result = new ArrayList<AuthenticationProviderModel>();
        for (AuthenticationProviderEntity entity : copy) {
            result.add(new AuthenticationProviderModel(entity.getProviderName(), entity.isPasswordUpdateSupported(), entity.getConfig()));
        }

        return result;
    }

    @Override
    public void setAuthenticationProviders(List<AuthenticationProviderModel> authenticationProviders) {
        List<AuthenticationProviderEntity> newEntities = new ArrayList<AuthenticationProviderEntity>();
        int counter = 1;
        for (AuthenticationProviderModel model : authenticationProviders) {
            AuthenticationProviderEntity entity = new AuthenticationProviderEntity();
            entity.setProviderName(model.getProviderName());
            entity.setPasswordUpdateSupported(model.isPasswordUpdateSupported());
            entity.setConfig(model.getConfig());
            entity.setPriority(counter++);
            newEntities.add(entity);
        }

        // Remove all existing first
        Collection<AuthenticationProviderEntity> existing = realm.getAuthenticationProviders();
        Collection<AuthenticationProviderEntity> copy = new ArrayList<AuthenticationProviderEntity>(existing);
        for (AuthenticationProviderEntity apToRemove : copy) {
            existing.remove(apToRemove);
            em.remove(apToRemove);
        }

        // Now create all new providers
        for (AuthenticationProviderEntity apToAdd : newEntities) {
            existing.add(apToAdd);
            em.persist(apToAdd);
        }

        em.flush();
    }

    @Override
    public Role getRole(String name) {
        TypedQuery<RoleEntity> query = em.createNamedQuery("getRealmRoleByName", RoleEntity.class);
        query.setParameter("name", name);
        query.setParameter("realm", realm);
        List<RoleEntity> roles = query.getResultList();
        if (roles.size() == 0) return null;
        return new RoleAdapter(provider, em, roles.get(0));
    }

    @Override
    public Role addRole(String id, String name) {
        RoleEntity entity = new RoleEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setRealm(realm);
        entity.setRealmId(realm.getId());
        em.persist(entity);
        realm.getRoles().add(entity);
        em.flush();
        return new RoleAdapter(provider, em, entity);
    }

    @Override
    public boolean removeRole(Role role) {
        if (role == null) {
            return false;
        }
        if (!role.getContainer().equals(this)) return false;

        RoleEntity roleEntity = ((RoleAdapter) role).getRole();
        realm.getRoles().remove(role);
        realm.getDefaultRoles().remove(role);

        em.createNativeQuery("delete from CompositeRole where childRole = :role").setParameter("role", roleEntity).executeUpdate();
        em.createQuery("delete from " + ScopeMappingEntity.class.getSimpleName() + " where role = :role").setParameter("role", roleEntity).executeUpdate();

        em.remove(roleEntity);

        return true;
    }

    @Override
    public Set<Role> getRoles() {
        Set<Role> list = new HashSet<Role>();
        Collection<RoleEntity> roles = realm.getRoles();
        if (roles == null) return list;
        for (RoleEntity entity : roles) {
            list.add(new RoleAdapter(provider, em, entity));
        }
        return list;
    }

    @Override
    public PasswordPolicy getPasswordPolicy() {
        if (passwordPolicy == null) {
            passwordPolicy = new PasswordPolicy(realm.getPasswordPolicy());
        }
        return passwordPolicy;
    }

    @Override
    public void setPasswordPolicy(PasswordPolicy policy) {
        this.passwordPolicy = policy;
        realm.setPasswordPolicy(policy.toString());
        em.flush();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Realm)) return false;

        Realm that = (Realm) o;
        return that.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String getLoginTheme() {
        return realm.getLoginTheme();
    }

    @Override
    public void setLoginTheme(String name) {
        realm.setLoginTheme(name);
        em.flush();
    }

    @Override
    public String getAccountTheme() {
        return realm.getAccountTheme();
    }

    @Override
    public void setAccountTheme(String name) {
        realm.setAccountTheme(name);
        em.flush();
    }

    @Override
    public String getAdminTheme() {
        return realm.getAdminTheme();
    }

    @Override
    public void setAdminTheme(String name) {
        realm.setAdminTheme(name);
        em.flush();
    }

    @Override
    public String getEmailTheme() {
        return realm.getEmailTheme();
    }

    @Override
    public void setEmailTheme(String name) {
        realm.setEmailTheme(name);
        em.flush();
    }

    @Override
    public boolean isAuditEnabled() {
        return realm.isAuditEnabled();
    }

    @Override
    public void setAuditEnabled(boolean enabled) {
        realm.setAuditEnabled(enabled);
        em.flush();
    }

    @Override
    public long getAuditExpiration() {
        return realm.getAuditExpiration();
    }

    @Override
    public void setAuditExpiration(long expiration) {
        realm.setAuditExpiration(expiration);
        em.flush();
    }

    @Override
    public Set<String> getAuditListeners() {
        return realm.getAuditListeners();
    }

    @Override
    public void setAuditListeners(Set<String> listeners) {
        realm.setAuditListeners(listeners);
        em.flush();
    }

    @Override
    public Application getMasterAdminApp() {
        return new ApplicationAdapter(provider, em, realm.getMasterAdminApp());
    }

    @Override
    public void setMasterAdminApp(Application app) {
        realm.setMasterAdminApp(((ApplicationAdapter) app).getJpaEntity());
        em.flush();
    }

}
