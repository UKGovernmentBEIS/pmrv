const baseConfig = require('../../jest.config');

module.exports = {
  ...baseConfig,
  rootDir: '../../',
  roots: ['<rootDir>/projects'],
};
