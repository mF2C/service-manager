function registerService(){
	var name = document.getElementById("name").value;
	var description = document.getElementById("description").value;
	var exec = document.getElementById("exec").value;
	var exec_type = document.getElementById("exec_type").value;
	var agent_type = document.getElementById("agent_type").value;
	var exec_ports;
	var exec_ports_string = document.getElementById("exec_ports").value;
	if(exec_ports_string == "")
        exec_ports = [];
    else
        exec_ports = exec_ports_string.split(',').map(function(item) {return parseInt(item, 10);});
	var num_agents = document.getElementById("num_agents").value;
	var cpu_arch = document.getElementById("cpu_arch").value;
	var os = document.getElementById("os").value;
	var memory_min = document.getElementById("memory_min").value;
	var storage_min = document.getElementById("storage_min").value;
	var disk = document.getElementById("disk").value;
	var req_resource_string = document.getElementById("req_resource").value;
	var req_resource;
	if(req_resource_string == "")
        req_resource = [];
    else
        req_resource = req_resource_string.split(',');
    var opt_resource;
	var opt_resource_string = document.getElementById("opt_resource").value;
	if(opt_resource_string == "")
        opt_resource = [];
    else
        opt_resource = opt_resource_string.split(',');

	var serviceJson = JSON.stringify({
		name: name,
		description: description,
		exec: exec,
		exec_type: exec_type,
		agent_type: agent_type,
		exec_ports: exec_ports,
		num_agents: num_agents,
		cpu_arch: cpu_arch,
		os: os,
		memory_min: memory_min,
		storage_min: storage_min,
		disk: disk,
		req_resource: req_resource,
		opt_resource: opt_resource
	});
	createService(serviceJson);
}

function createService(service)
{
	try
	{
		var response = null;
		$.ajax
		({
			data: service,
			url:   "http://localhost:46200/api/service-management/gui",
			type:  "POST",
			contentType: "application/json",
			async: false,
			success: function(ans)
			{
				response = ans;
			}
		});
		if (response['status'] == 201){
			alert("service registered correctly");
			window.location.href = "../index.html";
		} else{
			alert(response['message']);
		}
	}
	catch (e){return 0;}
}

function registerServiceCancel(){
	window.location.href = "../index.html";
}

window.onload=function(){
	execTypeFunction();
};

function execTypeFunction() {
	var type = document.getElementById("exec_type").value;
	if(type == "docker"){
		document.getElementById("exec_ports").disabled = false;
	} else {
		document.getElementById("exec_ports").disabled = true;
		document.getElementById("exec_ports").value = "";
	}
}


