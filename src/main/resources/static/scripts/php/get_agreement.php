<?php
	$service = $_GET['service'];
	$ch = curl_init('https://dashboard.mf2c-project.eu/api/agreement?$filter=name="'.$service.'"');
	curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");                                             
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);   
	curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json','slipstream-authn-info: internal ADMIN'));
	$result = curl_exec($ch);
	curl_close($ch);
	$obj = json_decode($result);
	$res = $obj->count;
	if ($res == 0){
		echo 0;
	}
	else{	
		$agreements = $obj->agreements;
		echo $agreements[0]->id;
	}
?>