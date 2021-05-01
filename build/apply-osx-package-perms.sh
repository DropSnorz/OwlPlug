JPackagePath=$(which jpackage)
sqlite3 "$HOME/Library/Application Support/com.apple.TCC/TCC.db" "insert into access (service, client, client_type, allowed, prompt_count, indirect_object_identifier_type, indirect_object_identifier) values ('kTCCServiceAppleEvents', '$JPackagePath', 1, 1, 1, 0, 'com.apple.finder')"
