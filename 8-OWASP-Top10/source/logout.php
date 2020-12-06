<?php
$_SESSION = array();
session_destroy();
header("LOCATION: "./content/login.php);
?>
