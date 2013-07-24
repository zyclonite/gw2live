<?php
/*
 * gw2live - GuildWars 2 Dynamic Map
 * 
 * Website: http://gw2map.com
 *
 * Copyright 2013   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
$required_fields = array("name", "email", "message");
foreach ($required_fields as $field) {
    if (strlen($_POST[$field]) <= 0) {
        echo "$field cannot be empty";
        die();
    }
}

$m = new Mongo();
$db = $m->selectDB("gw2live");
try {
	$collection = $db->selectCollection("feedback");
} catch (Exception $e) {
	$collection = $db->createCollection("feedback");
}

$feedback = array("name" => $_POST["name"], "email" => $_POST["email"], "message" => $_POST["message"], "ip" => $_SERVER['REMOTE_ADDR']);
$collection->insert($feedback);

echo "Thank you for your feedback!";
