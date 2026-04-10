import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8081',
  realm: 'badgerpass',
  clientId: 'badgerpass-web',
});

export default keycloak;