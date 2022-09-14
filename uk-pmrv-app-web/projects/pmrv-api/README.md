# PmrvApi

This library wraps the OpenAPI generator's results based on the current [swagger.yml](src/assets/swagger.yaml) file.

## Usage

Import any part of the generated code from `pmrv-api`. E.g.

```Typescript
import { SomeService } from 'pmrv-api';
```

## Code generation

In order to generate an updated version of the library, replace the current swagger file with the new one and generate the new services.

Find the swagger ui projection, for example http://localhost:8080/api/swagger-ui/

On the top of the page, find a link to the raw file, for example http://localhost:8080/api/v3/api-docs

Open the raw file, copy its contents and format them into yaml, for example using https://editor.swagger.io/

Copy the contents and paste/replace the old swagger.yaml found [here](src/assets/swagger.yaml)

Run code generation

```shell script
yarn generate:api
```

Build the pmrv-api library

```shell script
ng build pmrv-api
```

Build, serve and test the app. There should be few to no problems unless we know beforehand that there will be breaking changes.
If more changes need to be done, proceed to fix the app.

Commit your working changes using a relevant message, for example `"chore(api): generation"`

Use `--no-verify` as a last resort to bypass the message validation if left with no choice

```shell script
git commit -m "chore(api): generation" --no-verify
```

If you intend to use `--no-verify`, manually run linter and prettier fixes first

```shell script
yarn lint --fix && yarn pretty-quick --staged
```
