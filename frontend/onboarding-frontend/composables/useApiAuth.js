// composables/useApiAuth.js
export const useApiAuth = (url, options = {}) => {
  
  const token = process.client ? localStorage.getItem('keycloak_token') : null;

  return useFetch(url, {
    ...options,
    headers: {
      ...options.headers,
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    }
  })
}