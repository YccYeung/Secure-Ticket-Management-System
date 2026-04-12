import Keycloak from 'keycloak-js'

export const authFetch = async (keycloak: Keycloak, url: string, options: RequestInit = {}) => {
  await keycloak.updateToken(30)
  return fetch(url, {
    ...options,
    headers: {
      'Authorization': `Bearer ${keycloak.token}`,
      'Content-Type': 'application/json',
      ...options.headers
    }
  })
}