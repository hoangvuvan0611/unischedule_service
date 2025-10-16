module.exports = {
  apps: [

    {
      name: "springboot-gold-service",
      script: "java",
      args: "-jar target/gold_price_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod",
      cwd: "/Users/vuvanhoang/WorkSpace/project/gold_price/gold_price_service" 
    }
  ]
};
