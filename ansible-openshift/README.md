Att köra en playbook mot sandbox:

    ansible-playbook -i "localhost," query.yml -c local --extra-vars "api_endpoint=<url to sandbox>"