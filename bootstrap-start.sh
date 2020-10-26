#To install symbol-bootstrap https://github.com/nemtech/symbol-bootstrap

symbol-bootstrap start -t target/bootstrap -c bootstrap-preset.yml --healthCheck --timeout 120000 -r -u current $1
