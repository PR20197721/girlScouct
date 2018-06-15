echo "Connecting..."
ssh -i $KEY_LOCATION eyuser@$1 'bash -s' < $SCRIPT_LOCATION/$2