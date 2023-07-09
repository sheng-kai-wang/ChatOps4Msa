import http from 'k6/http';
import {sleep} from 'k6';

export const options = {
  // Key configurations for Stress in this section
  stages: [
    { duration: '5s', target: 200 }, // traffic ramp-up from 1 to a higher 150 users over 5s.
    { duration: '20s', target: 200 }, // stay at higher 150 users for 20s
    { duration: '5s', target: 0 }, // ramp-down to 0 users
  ],
};

export default () => {
  // const testUrl = "https://test-api.k6.io";
  const testUrl = __ENV.TEST_URL;
  http.get(testUrl);
  sleep(1);
};
