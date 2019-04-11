window.onload=function(){
	execTypeFunction();
	getSlaTemplates();
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

function getSlaTemplates(){
	try {
        $.ajax
        ({
            url:   "api/service-management/gui/sla-template",
            type:  "GET",
            async: false,
            success: function (ans) { loadSlaTemplates(ans); }
        });
    } catch (e){return 0;}
}

function loadSlaTemplates(response){
    if (response != null){
        if(response['status'] == 200) {
            var slaTemplates = response['templates'];
            var slaTemplateSelect = document.getElementById('sla_template');
            for (var i = 0; i < slaTemplates.length; i++) {
                var opt = document.createElement('option');
                opt.value = slaTemplates[i]['id'];
                opt.innerHTML = slaTemplates[i]['name'];
                slaTemplateSelect.appendChild(opt);
            }
        } else {
            alert("SLA templates error: " + response['message']);
        }
    }
}

function registerService(){
	var name = document.getElementById("name").value;
	var description = document.getElementById("description").value;
	var exec = document.getElementById("exec").value;
	var exec_type = document.getElementById("exec_type").value;
	var agent_type = document.getElementById("agent_type").value;
	var sla_template = document.getElementById("sla_template").value;
	var exec_ports;
	var exec_ports_string = document.getElementById("exec_ports").value;
	if(exec_ports_string !== "")
        exec_ports = exec_ports_string.split(',').map(function(item) {return parseInt(item, 10);});
	var num_agents = document.getElementById("num_agents").value;
	var cpu_arch = document.getElementById("cpu_arch").value;
	var os = document.getElementById("os").value;
	var memory_min = document.getElementById("memory_min").value;
	var storage_min = document.getElementById("storage_min").value;
	var disk = document.getElementById("disk").value;
	var req_resource_string = document.getElementById("req_resource").value;
	var req_resource;
	if(req_resource_string !== "")
        req_resource = req_resource_string.split(',');
    var opt_resource;
	var opt_resource_string = document.getElementById("opt_resource").value;
	if(opt_resource_string !== "")
        opt_resource = opt_resource_string.split(',');
	var serviceJson = {
		name: name,
		description: description,
		exec: exec,
		exec_type: exec_type,
		sla_templates: [sla_template],
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
	}
	var refactoredService = JSON.stringify(serviceJson, (key, value) => {
      if (value !== "" && value !== null) return value
    })
	createService(refactoredService);
}

function createService(service)
{
	try
	{
		var response = null;
		$.ajax
		({
			data: service,
			url:   "api/service-management/gui",
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
			window.location.href = "index.html";
		} else{
			alert(response['message']);
		}
	}
	catch (e){return 0;}
}

function registerServiceCancel(){
	window.location.href = "index.html";
}