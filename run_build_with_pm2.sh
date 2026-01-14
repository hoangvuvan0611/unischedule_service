echo 'Delete service running in PM2'
pm2 delete springboot-unischedule-service
echo 'Compile jar file'
mnv clean package -DskipTest
echo 'Compile complete'
echo 'Run Service'
pm2 start ./pm2_ecosystem.config.js