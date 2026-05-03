import { extend } from 'umi-request';

export const request = extend({
  prefix: process.env.API_BASE_URL || '',
  timeout: 60000,
});
