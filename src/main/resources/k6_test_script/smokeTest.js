import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  vus: 3, // Key for Smoke test. Keep it at 2, 3, max 5 VUs
  duration: '10s', // This can be shorter or just a few iterations
};

export default () => {
  const testUrl = __ENV.TEST_URL;
  http.get(testUrl);
  sleep(1);
};