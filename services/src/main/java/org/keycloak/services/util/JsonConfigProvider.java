package org.keycloak.services.util;

import org.codehaus.jackson.JsonNode;
import org.keycloak.Config;
import org.keycloak.util.StringPropertyReplacer;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class JsonConfigProvider implements Config.ConfigProvider {

    private JsonNode config;

    public JsonConfigProvider(JsonNode config) {
        this.config = config;
    }

    @Override
    public String getProvider(String spi) {
        JsonNode n = getNode(spi, "provider");
        return n != null ? StringPropertyReplacer.replaceProperties(n.getTextValue()) : null;
    }

    @Override
    public Config.Scope scope(String... path) {
        return new JsonScope(getNode(path));
    }

    private JsonNode getNode(String... path) {
        JsonNode n = config;
        for (String p : path) {
            n = n.get(p);
            if (n == null) {
                return null;
            }
        }
        return n;
    }

    public class JsonScope implements Config.Scope {

        private JsonNode config;

        public JsonScope(JsonNode config) {
            this.config = config;
        }

        @Override
        public String get(String key) {
            return get(key, null);
        }

        @Override
        public String get(String key, String defaultValue) {
            if (config == null) {
                return defaultValue;
            }
            JsonNode n = config.get(key);
            if (n == null) {
                return defaultValue;
            }
            return StringPropertyReplacer.replaceProperties(n.getTextValue());
        }

        @Override
        public String[] getArray(String key) {
            if (config == null) {
                return null;
            }

            JsonNode n = config.get(key);
            if (n == null) {
                return null;
            } else if (n.isArray()) {
                String[] a = new String[n.size()];
                for (int i = 0; i < a.length; i++) {
                    a[i] = StringPropertyReplacer.replaceProperties(n.get(i).getTextValue());
                }
                return a;
            } else {
               return new String[] { StringPropertyReplacer.replaceProperties(n.getTextValue()) };
            }
        }

        @Override
        public Integer getInt(String key) {
            return getInt(key, null);
        }

        @Override
        public Integer getInt(String key, Integer defaultValue) {
            if (config == null) {
                return defaultValue;
            }
            JsonNode n = config.get(key);
            if (n == null) {
                return defaultValue;
            }
            if (n.isTextual()) {
                return Integer.parseInt(StringPropertyReplacer.replaceProperties(n.getTextValue()));
            } else {
                return n.getIntValue();
            }
        }

        @Override
        public Long getLong(String key) {
            return getLong(key, null);
        }

        @Override
        public Long getLong(String key, Long defaultValue) {
            if (config == null) {
                return defaultValue;
            }
            JsonNode n = config.get(key);
            if (n == null) {
                return defaultValue;
            }
            if (n.isTextual()) {
                return Long.parseLong(StringPropertyReplacer.replaceProperties(n.getTextValue()));
            } else {
                return n.getLongValue();
            }
        }

        @Override
        public Boolean getBoolean(String key) {
            return getBoolean(key, null);
        }

        @Override
        public Boolean getBoolean(String key, Boolean defaultValue) {
            if (config == null) {
                return defaultValue;
            }
            JsonNode n = config.get(key);
            if (n == null) {
                return defaultValue;
            }
            if (n.isTextual()) {
                return Boolean.parseBoolean(StringPropertyReplacer.replaceProperties(n.getTextValue()));
            } else {
                return n.getBooleanValue();
            }
        }
    }

}
