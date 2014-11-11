# Adding Kibana examples

# To export a dashboard use:
# curl -s -S http://localhost:9200/kibana-int/dashboard/kibana-demo1/_source -o kibana-demo1.json


for f in *.json ; do 
	echo "Injecting $f" 
	curl -s -XPUT "http://localhost:9200/kibana-int/dashboard/${f%.json}" --data-binary @"$f" -o /dev/null
done

