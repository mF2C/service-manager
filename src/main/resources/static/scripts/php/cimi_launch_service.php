<?php
$service_instance = $_POST['serviceInstance'];
$ch = curl_init('https://dashboard.mf2c-project.eu:46000/api/v1/lifecycle'); 
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
curl_setopt($ch, CURLOPT_POSTFIELDS, $service_instance);                                             
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);  
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json','Content-Length: ' . strlen($service_instance)));
$result = curl_exec($ch);
curl_close($ch);
$obj = json_decode($result);
echo $obj->message;
?>