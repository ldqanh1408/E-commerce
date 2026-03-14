#!/bin/sh
LOG_FILE="/shared/stripe.log"
echo "Relay Service is waiting for Stripe Webhook Secret..."

# Wait until the log file exists and contains "whsec_"
while [ ! -f "$LOG_FILE" ] || ! grep -q "whsec_" "$LOG_FILE"; do
  sleep 1
done

STRIPE_WH_SECRET=$(grep -o 'whsec_[^ ]*' "$LOG_FILE" | head -1)
echo "✅ Secret captured: $STRIPE_WH_SECRET"

export STRIPE_WEBHOOK_SECRET=$STRIPE_WH_SECRET

echo "Starting Webhook Relay Spring Boot App..."
exec java -jar app.jar
