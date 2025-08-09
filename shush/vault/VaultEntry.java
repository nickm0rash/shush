package shush.vault;

public class VaultEntry {
        private final String username;
        private final String password;
        private final boolean requires2FA;

        public VaultEntry(String label, String username, String password, String comment, boolean requires2FA) {
            this.username = username;
            this.password = password;
            this.requires2FA = requires2FA;
        }

        public String toJson() {
            return String.format("{\"username\":\"%s\",\"password\":\"%s\",\"2fa\":%b}",
                    username, password, requires2FA);
        }
    }