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
$version = '1.0.0.0';
$file = '../downloads/Gw2Map.exe';

function anonymip($ip) {
	$returnstring = '';
	if(strstr($ip, '.')) {
		//ip4
		$seperator = '.';
	}else{
		//ip6
		$seperator = ':';
	}
	$ipparts = explode($seperator,$ip);
	for($i = 0; $i < (count($ipparts)-1); $i++){
		$returnstring .= $ipparts[$i].$seperator;
	}
	return $returnstring.'*';
}

if($_GET['checkversion']) {
	echo $version;
	exit();
}

$m = new Mongo();
$db = $m->selectDB("gw2live");
try {
    $collection = $db->selectCollection("downloads");
} catch (Exception $e) {
    $collection = $db->createCollection("downloads");
}

if($_GET['stats']) {
	echo "Total downloads: ".$collection->count()."<br>";
        echo "<br>Today<br>";
        $query = array('timestamp' => array('$gt' => strtotime('today'), '$lt' => strtotime('now')));
        $cursor = $collection->find($query);
        foreach ($cursor as $doc) {
                echo date("Y-m-d H:i:s",$doc['timestamp'])." from ".anonymip($doc['ip'])."<br>";
        }
	echo "<br>Yesterday<br>";
	$query = array('timestamp' => array('$gt' => strtotime('yesterday'), '$lt' => strtotime('today')));
	$cursor = $collection->find($query);
	foreach ($cursor as $doc) {
		echo date("Y-m-d H:i:s",$doc['timestamp'])." from ".anonymip($doc['ip'])."<br>";
	}
	exit();
}

if (file_exists($file)) {
    $download = array("timestamp" => time(), "ip" => $_SERVER['REMOTE_ADDR']);
    $collection->insert($download);

    header('Content-Description: File Transfer');
    header('Content-Type: application/octet-stream');
    header('Content-Disposition: attachment; filename='.basename($file));
    header('Content-Transfer-Encoding: binary');
    header('Expires: 0');
    header('Cache-Control: must-revalidate');
    header('Pragma: public');
    header('Content-Length: ' . filesize($file));
    ob_clean();
    flush();
    readfile($file);
    exit;
}else{
    echo "download service offline";
    exit;
}
