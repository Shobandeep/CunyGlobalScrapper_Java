# CunyGlobalScrapper_Java
Web Scrapper retrieves classes available for the Computer Science department of Queens College

WARNING: Be nice. DON'T refresh the page rapidly and DON'T violate cuny's ToS https://www.cuny.edu/wp-content/uploads/sites/4/page-assets/website/privacy-policy/ComputerUsePolicy.pdf, cuny offers a this free service to check class schedules, let's not ruin it for everyone.

Uses HTMLUnit to navigate https://globalsearch.cuny.edu/CFGlobalSearchTool/search.jsp and extract classes and then Java Mail API to send notifications.

Does the same thing as it's JavaScript counterpart but also notifies the user that their class is open. 

NOTE: Program requires the JavaMail API to work, it can be found here: https://www.oracle.com/technetwork/java/javamail/index.html

it also needs HTMLUnit which is used to navigate the webpages: http://htmlunit.sourceforge.net/
