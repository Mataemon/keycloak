package org.keycloak.authentication;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AuthenticationSpi implements Spi {

    @Override
    public String getName() {
        return "authentication";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return AuthenticationProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return AuthenticationProviderFactory.class;
    }

}
