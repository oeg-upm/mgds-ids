#!/bin/sh
# Recreate config file
rm -rf ./env-config.js
touch ./env-config.js
function join_by { local IFS="$1"; shift; echo "$*"; }

echo I am $(whoami)

# Find vue env vars
vars=$(env | grep VUE_APP_ | awk -F = '{print "$"$1}')
vars=$(join_by ',' $vars)
echo "Found variables $vars"

#echo " VUE_APP_BACKEND_BASE_URL: '${VUE_APP_BACKEND_BASE_URL}' , VUE_APP_CONFIG_MANAGER_BASE_URL:${VUE_APP_CONFIG_MANAGER_BASE_URL} " >> .env
for file in /usr/share/nginx/html/js/app.*.js;
do
  echo "Processing $file ...";

  # Use the existing JS file as template
  if [ ! -f $file.tmpl.js ]; then
    cp $file $file.tmpl.js
  fi
  envsubst "$vars" < $file.tmpl.js > $file
  rm $file.tmpl.js
done


echo "Starting nginx"
nginx -g 'daemon off;'