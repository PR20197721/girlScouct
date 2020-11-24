url = '34.203.143.115:4503/content/gsusa/join' //'www.comgirlscouts.org/bronze'
"curl -sIL -o /dev/null -w %{http_code} ${url}".execute().in.text
