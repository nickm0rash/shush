
# 🔐 Shush

**Shush** is a secure, self-hosted, syncing, and scriptable CLI-based password manager written in Java. Designed for power users, homelabbers, and privacy enthusiasts, Shush prioritizes **security first**.

## PROJECT COMPLETION ~5%

## 🚀 Features (Coming Soon) 

- 🔒 Local or remote vault storage with optional syncing
- 🔐 Vault-level TOTP (2FA) and/or PIN authentication
- 📎 Clipboard autofill (default), or optional plaintext view (`-v`)
- 📁 CSV import from common password managers (with warnings)
- 🔍 Searchable vault entries
- 🧠 Custom password generator
- ✅ Compromised password checker
- 🔧 Full config-style behavior like `sshd_config`
- 🧱 Optional multi-user/team support
- 📲 Optional Twilio (or other API) SMS 2FA
- 📤 Self-hosted `shush-server` for remote vault access

## 📦 Installation

> Native image support (via GraalVM) coming soon for easy portable binaries.

## 🧰 Commands

```
shush init               Initialize a new vault
shush add                Add a password
shush get                Get password (copied to clipboard)
shush rm                 Remove a password
shush update             Update existing entry
shush search <keyword>   Search vault
shush connect            Connect to remote shush-server
shush pin                Update or set PIN
shush gen <label> -24    Generate password of N length
shush import <file.csv>  Import CSV (cleared after use)
shush dbname --check-compromised   Check for weak or leaked passwords
shush topt-add           Enable TOTP 2FA after init
shush topt-remove        Disable TOTP 2FA
shush topt-update        Change TOTP configuration
```

## 🔐 Security Philosophy

Shush is built with the belief that:

- Your vault should never rely on the cloud unless **you** host it
- No GUI = fewer attack surfaces
- TOTP or PIN should protect your data even on compromised systems
- Passwords are never printed unless explicitly requested
- Keys and sensitive data are kept **in RAM only** when needed

> ⚠️ CSV files are treated as insecure and are deleted by default after import.

## 🛠️ Development Roadmap

- [x] CLI interface and command structure
- [ ] Vault init, add, get, rm
- [ ] Clipboard support
- [ ] CSV import and secure deletion
- [ ] PIN and TOTP support
- [ ] Custom password generator
- [ ] Native image builds (GraalVM)
- [ ] Frontend dashboard (optional)
- [ ] Yubikey support
- [ ] Integration with distros/repos
- [ ] Auto-sync between machines

## 💡 Use Cases

- Developers managing SSH credentials across servers
- Homelab admins who want offline-first password control
- Privacy-focused users who don’t want GUI-based managers
- Terminal nerds who want something fast and secure

## ❤️ Credits & License

Created by [Nicholas Morash](https://github.com/nmorash), Nova Scotia-based full-stack privacy nerd.

Licensed under the **MIT License**. Contributions welcome!
