const { pathsToModuleNameMapper } = require('ts-jest');
// In the following statement, replace `./tsconfig` with the path to your `tsconfig` file
// which contains the path mapping (ie the `compilerOptions.paths` option):
const { compilerOptions } = require('./tsconfig');

module.exports = {
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  cacheDirectory: 'tmp/jest/cache',
  modulePathIgnorePatterns: ['<rootDir>/dist'],
  moduleNameMapper: {
    ...pathsToModuleNameMapper(compilerOptions.paths, {
      prefix: '<rootDir>/',
    }),
    '^lodash-es$': 'lodash',
  },
  transformIgnorePatterns: ['/node_modules/(?!zxcvbn3)'],
};
