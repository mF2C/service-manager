<?php

$ch = curl_init('https://dashboard.mf2c-project.eu/api/service');             
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");                                             
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);   
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json','slipstream-authn-info: internal ADMIN'));
$result = curl_exec($ch);
curl_close($ch);
$obj = json_decode($result);
//echo $result;
$services = $obj->services;
echo json_encode($services);
?>