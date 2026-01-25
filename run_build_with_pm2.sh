echo 'Pull code updated'
git pull origin main
echo 'Delete service running in PM2'
pm2 delete springboot-unischedule-service
echo 'Compile jar file'
mvn clean package -DskipTest
echo 'Compile complete'
echo 'Run Service'
pm2 start ./pm2_ecosystem.config.js
pm2 logs springboot-unischedule-service