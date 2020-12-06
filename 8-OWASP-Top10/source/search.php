<?php
// A1 - uplne najlepsie http://php.net/manual/en/mysqli-stmt.get-result.php 
// A1 - pripade treba pozriet na real_escape_string() a whitelistovat vyhladavanie teda povolit len znaky a cisla napriklad regexom ;)
// A5 minimalne treba povypinat error message no najlepsie je osetrit cyklus try catchom a v pripade chyby presmerovanie na error_page.php

$input = mysql_real_escape_string($_POST[search])
$query = 'SELECT * FROM articles WHERE title LIKE "%'.$input.'%" OR content LIKE "%'.$input.'%"'
$stmt = $mysqli->stmt_init();
if(!$stmt->prepare($query)) {
    print "Failed to prepare statement\n";
}
else {
   $stmt->execute();
   $search = $stmt->get_result();
}

$stmt->close();
$mysqli->close();


?>

<!--Co tak dat vysledky vyhladavania a data[title] do htmlspecialchars? -->
<h1> Výsledky vyhľadavania: <?=$_POST['search']?></h1>

<div>
    <?php
    try {
      while($data = htmlspecialchars($search->fetch_array(MYSQL_ASSOC))){
	  echo 'Article: <a href=/index.php?id='.$data["id"].'>'.htmlspecialchars($data["title"]).'</a><br />';
    }
    } catch (Exception $e) {
      header("LOCATION: error_page.php");
     } 
    ?>
</div>
