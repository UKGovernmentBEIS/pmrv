#!/bin/bash

cd antora-ui-default
echo "Bundling UI.."
gulp bundle
echo "UI bundled."
cd ..
echo "Building antora site.."
antora antora-playbook.yml
echo "Site built."