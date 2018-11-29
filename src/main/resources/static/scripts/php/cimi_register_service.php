<?php
$service = $_POST['service'];

$ch = curl_init('https://dashboard.mf2c-project.eu/api/service');             
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");                                                                    
curl_setopt($ch, CURLOPT_POSTFIELDS, $service);                                                                  
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);   
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json','slipstream-authn-info: internal ADMIN','Content-Length: ' . strlen($service)));
$result = curl_exec($ch);
curl_close($ch);
$obj = json_decode($result);
$status = $obj->status;
$message = $obj->message;

if($status == 201){
	echo $status;
} else {
	echo $message;
}

?>