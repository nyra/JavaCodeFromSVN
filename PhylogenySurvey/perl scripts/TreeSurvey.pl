#!/usr/bin/perl

# set some constants
$admin_pw = "lab09acce55";
$too_late_month = 2;
$too_late_day = 15;

use DBI;
use CGI;
use GradeDB;
use treeDB;

$script_url = "https://www.securebio.umb.edu/cgi-bin/TreeSurvey.pl";

@date = loacltime(time);

# if we enter without params, give the student login page
#  otherwise, process survey data
if ($ENV{'CONTENT_LENGTH'} == 0) {
	&login_page;
} else {
	&load_survey;
}

exit 1;

#------------------

sub login_page {
	print "Content-type: text/html\n\n";
	print "<html><head>\n";
	print "<title>Login to the Phylogeny Survey</title>\n";
	print "</head>\n";
	print "<body bgcolor = \"yellow\">\n";
	
	print "<form action=\"$script_url\" method=\"POST\">\n";

	$dbh = GradeDB::connect();
	
	print "<font size=+3>Login to the diversity of life survey site</font><br>\n";
	print "Choose your name from this list:<br>\n";
    print "<select name=\"Name\" size=12>\n";
	$sth = $dbh->prepare("SELECT * FROM students ORDER BY name");
    $sth->execute();
    while (@result = $sth->fetchrow_array()) {
        print "<option value=\"$result[0]\">$result[0]</option>\n";
    }
    $sth->finish();
    print "</select><br>\n";
    print "Enter your 8-digit UMS ID # (leave off the UMS):\n";
    print "<input type=\"password\" name=\"Passwd\" size=20><br>\n";
    print "  <input type=\"submit\" value=\"Login\">\n";
    
	print "</form>\n";
	
	print "</body></html>\n";
}

sub load_survey {

	$query = CGI->new();
	$name = $query->param('Name');
	$password = $query->param('Passwd');
	$Q1 = $query->param('Q1');
	$Q2 = $query->param('Q2');
	$treeXML = $query->param('treeXML');
	
	#read in the parts of Q3
	$Q3 = "";
	for ($i = 0; $i < 12; $i++) {
	     $part = $query->param("Q3-$i");
	     if ($part eq "") {
	          $part = "0";
	     }
	     $Q3 = $Q3.$part.",";
	}
	$last = $query->param("Q3-12");
	if ($last eq "") {
	     $last = "0";
	}
	$Q3 = $Q3.$last;
	

	print "Content-type: text/html\n\n";
	print "<html><head>\n";
	print "<title>Diversity of Life Survey for $name</title>\n";
	
	if ($password eq $admin_pw) {
	     $admin_mode = 1;
	} else {
	     $admin_mode = 0;
	
	     $dbh = GradeDB::connect();
	     $statement = "SELECT password FROM students
 	          WHERE name=\"$name\"";
	     $sth = $dbh->prepare($statement);
	     $sth->execute();
	     @result = $sth->fetchrow_array();
	     $sth->finish();
	     $pw = $result[0];

	     if(&decrypt_pw($pw,$password) != 1){
	        print "<body bgcolor=red>\n";
  	     	print "<br><font color=green><b>Error: Password incorrect 
   	     	       for $name.</b></font><br>";
   	     	print "<a href=\"$script_url\">Click here to return to login screen</a>.\n";
   		     print "</body></html>\n";
   		     exit 1;
	     }
	     $dbh->disconnect();
	}
	
    #see if there's already an entry for this student
	$dbh = treeDB::connect();
	$statement = "SELECT count(*) from student_data WHERE name=?";
	$sth = $dbh->prepare($statement);
	$sth->execute($name);
	$rowcount = $sth->fetchrow();
	$sth->finish();

	#see if they're entering data
	if ($treeXML ne "") {
	
	    if ($rowcount == 0) {
	        $statement = "INSERT INTO student_data (Q1, Q2, Q3, tree, date, name) VALUES (?,?,?,?,NOW(),?)";
	    }  else {
	        $statement = "UPDATE student_data SET Q1 = ?, Q2 = ?, Q3 = ?, tree = ?, date = NOW() WHERE name = ?";
	    }
	    $sth = $dbh->prepare($statement);
	    $sth->execute($Q1, $Q2, $Q3, $treeXML, $name);
	    $sth->finish();
	    $dbh->disconnect();
	} else {
	    # if there's already data there, get it
	    if ($rowcount != 0) {
	        $sth = $dbh->prepare("SELECT Q1, Q2, Q3, tree FROM student_data WHERE name=?");
	        $sth->execute($name);
	        ($Q1, $Q2, $Q3, $treeXML) = $sth->fetchrow_array();
	        $sth->finish();
	    }
	}
	
	$dbh->disconnect();
	
	&setup_arrays;
	
    $version = "A";
	
	@Q3parts = split /,/, $Q3;
	
	print "<SCRIPT language=\"JavaScript\">\n";
	print "function getTreeData() {\n";
	print "    var xml = document.TreeApplet.getTreeXML();\n";
	print "    document.forms[0].treeXML.value = xml;\n";
	print "    return true;\n";
	print "}\n";
	print "function setTreeData() {\n";
	print "    document.TreeApplet.setTreeXML(\"$treeXML\");\n";
	print "}\n";
	print "</script>\n";
	print "</head>\n";
	print "<body bgcolor = \"lightblue\" onload=\"setTreeData()\">\n"; 
	
	print "<center><font size=+2>Diversity of Life Survey for $name</font></center><br>\n";
	print "This survey is designed to see how well you understand the diversity of living \n";
	print "organisms.  There is no right or wrong answer; you will receive full credit for \n";
	print "whatever you write.  We are most interested in your understanding of these \n";
	print "important biological issues.  <font color=red>Please do not consult any outside \n";
	print "sources (textbook ,www, other people, etc,) when completing this survey!!</font><hr>\n";
	print "Assume that you are working for a natural history museum like the Harvard Museum of \n";
	print "Natural History, only smaller.  Your museum has specimens of the following 20 \n";
	print "types of organisms in its collection.  Your task is to design a tree that will help \n";
	print "orient visitors to the collection.  \n";
	print "Your tree should include all the groups of organisms listed below and communicate the \n";
	print "ways they are evolutionarily related to one another.<br><br><br>\n";
	print "Using the program in the window\n";
	print "below, draw a tree diagram to show the relationships between these organisms.\n";
	print "Please include additional text and graphics that you think will help visitors \n";
	print "understand how you have organized these groups of organisms. There is no right or \n";
	print "wrong answer to this task, but it is important that you are able to explain the logic \n";
	print "behind your approach.<br><br>\n";

	print "<b>Instructions:</b><br>\n";
	print "<ul>\n";
  	print "<li>Drag Organisms to where you want them</li>\n";
  	print "<li>Shift-click on the workspace to add a node</li>\n";
  	print "<li>Click on an item (organism or node) to select it. It's border will turn red</li>\n";
  	print "<li>When you have two items (organism and/or node) selected, you can link or unlink them</li>\n";
  	print "<li>Links will remain connected when you move items.</li>\n";
  	print "<li>Click &quot;Label&quot; to add a text label.</li>\n";
  	print "<li>Click &quot;Delete&quot; to delete a test label or node.</li>\n";
  	print "<li>Select two objects connected by a link and click &quot;Split&quot; \n";
  	print "to add a new node in the middle of the link.</li>\n";
	print "</ul>\n";
  	print "<form action=\"$script_url\" method=\"POST\" onsubmit=\"return getTreeData();\" ";
  	print "name=\"form\">\n";
	print "<applet code=\"phylogenySurvey.SurveyApplet.class\" \n";
	print "archive=\"https://www.securebio.umb.edu/phylogenySurvey.jar\" \n";
	print "width=1020 height=1020 name=\"TreeApplet\">\n";
  	print "          You have to enable Java on your machine !</applet>\n";
    print "<br><br>\n";
    print "<hr>\n";
    print "<b>After you have developed your tree please answer the following questions.</b><br>\n";
    print "<b>1)</b> Explain in words how you went about organizing these organisms. Use one \n";
    print "or two specific examples and describe why you put them where you did.<br>\n";
    print "<textarea name=\"Q1\" rows=10 cols=80>$Q1</textarea><br><br>\n";
    print "<b>2)</b> How did you decide if organisms were closely related to one another\n";
    print "or not closely related? Use one or two specific examples from your work to explain \n";
    print "your reasoning.<br>\n";
    print "<textarea name=\"Q2\" rows=10 cols=80>$Q2</textarea><br><br>\n";
    print "<b>3) Common Ancestors:</b><br>\n";
    print "Choose the option that best expresses <u>how well you agree with</u> in the following";
    print " statements.  Use these options:<br>\n";
    print "<table border = 1>\n";
    print "<tr>\n";
    print "<td>&nbsp;</td>\n";
    print "<td><font color=red><b>Strongly Disagree</font></b></td>\n";
    print "<td><font color=red>Disagree</font></td>\n";
    print "<td>I\'m not sure</td>\n";
    print "<td><font color=green>Agree</font></td>\n";
    print "<td><font color=green><b> Strongly Agree</font></b></td>\n";
    print "</tr>\n";
    for ($i = 0; $i < 13; $i++){
         print "<tr>\n";
         print "<td>";
         if ($version eq "A") {
              print $VersionA[$i];
         } else {
              print $VersionB[$i];
         }
         print "</td>\n";
         for ($j = 1; $j < 6; $j++ ) {
              print "<td><input type=\"radio\" name=\"Q3-$i\" value=\"$j\"";
              if ($Q3parts[$i] == $j) {
                   print " CHECKED ";
              }
              print ">\n";
         }
         print "</tr>\n";
    }
    print "</table>\n";
    print "<input type=\"hidden\" name=\"Name\" value=\"$name\">\n";
    print "<input type=\"hidden\" name=\"Passwd\" value=\"$password\">\n";
    print "<input type=\"hidden\" name=\"treeXML\" value=\"$treeXML\">\n";
    @time = localtime(time);
    if (($admin_mode == 0) || ($too_late == 0)){
         print "<input type=\"submit\">\n";
    }
  	print "</form>\n";
  
}

sub decrypt_pw {

	my $cryptpw = $_[0];
	my $pw = $_[1];
	my $isok = 0;
	if(crypt($pw, time.$$) eq $cryptpw && $pw ne "" && $cryptpw ne ""){
		$isok = 1;
	}
	$isok;
}

sub setup_arrays {
	#set up for the 13 questions
	@VersionA = (	"Cucumbers and humans have a common ancestor.",
					"Tuna fish and pumpkins have a common ancestor.",
					"Earthworms, mosquitoes, and humans all have a common ancestor.",
					"Earthworms, leeches, and snails all have a common ancestor.",
					"Beetles, ants, and mosquitoes all have a common ancestor.",
					"Crocodiles, rodents, and turtles all have a common ancestor.",
					"Salamanders, lizards, and crocodiles all have a common ancestor.",
					"Dolphins and humans have a common ancestor.",
					"Rats, whales, and zebras all have a common ancestor.",
					"Rodents and dogs have a common ancestor.",
					"Monkeys, baboons, and humans all have a common ancestor.",
					"Dogs, coyotes, and wolves all have a common ancestor.",
					"Gorillas, monkeys, and chimpanzees all have a common ancestor.");
	@VersionB = reverse @VersionA;
}
