<?php
    $allowed = array(
      'index.php',
      'home.php',
      'login.php',
      'logout.php',
      'search.php',
      'kontakt.php'
    );
   if(isset($file))
   {
      if (in_array(basename($_GET['file']), $allowed)) {
         include('./content/' . basename($_GET['file']));
      }
   }
   else
   {
       include("index.php");
   }
?>
