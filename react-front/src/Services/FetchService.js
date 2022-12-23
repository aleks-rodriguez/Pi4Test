import 'regenerator-runtime/runtime';

function getBackURI() {
  let URI = '';
  if (process.env.NODE_ENV === 'development') {
    URI = 'http://localhost:8080/api/';
  } else {
    URI = 'https://pi4test.asuscomm.com/api/';
  }
  return URI;
}

function isExpired(jwt) {
  const jsonToken = atob(jwt.split('.')[1]);
  let tokenExpiration = JSON.parse(jsonToken).exp;
  tokenExpiration = Number.parseInt(`${tokenExpiration}000`, 10);

  return tokenExpiration < new Date().getTime();
}

async function fetchRefresh(oldJwt) {
  let refreshed = '';
  const URI = getBackURI();
  const token = { token: oldJwt };
  await fetch(`${URI}auth/refresh`, {
    method: 'POST',
    headers: new Headers({
      'Content-type': 'application/json',
    }),
    body: JSON.stringify(token),
  })
    .then(async (res) => {
      if (!res.ok) {
        throw new Error();
      } else {
        const json = await res.json();
        refreshed = json.token;
      }
    });
  return refreshed;
}

export async function getTokenFromAPI(jwt) {
  const expired = isExpired(jwt);
  let token = null;
  if (expired) {
    token = await fetchRefresh(jwt);
    return token;
  }
  return jwt;
}

export async function fetchBack(resource, options) {
  const URI = getBackURI();
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), 300000);
  const response = await fetch(`${URI}${resource}`, {
    ...options,
    signal: controller.signal,
  });
  clearTimeout(timer);
  return response;
}

export async function fetchLogout(jwt) {
  const URI = getBackURI();
  let ret;
  await fetch(`${URI}auth/logout`, {
    method: 'GET',
    headers: new Headers(
      {
        'Content-type': 'application/json',
        authorization: `Bearer ${jwt}`,
      },
    ),
  })
    .then(async (res) => {
      if (!res.ok) {
        ret = false;
      }
      ret = true;
    });
  return ret;
}
