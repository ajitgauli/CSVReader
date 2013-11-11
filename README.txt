Setup:
Create two tables in using these sql commands:

//This table was populated by test-data.csv (included in this zip)

CREATE TABLE student_year_2013 (
id BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
residing_city VARCHAR(75) not null,
residing_county VARCHAR(75) not null,
residing_state VARCHAR(20) not null,
residing_zip VARCHAR(20) not null,
home_city VARCHAR(75) not null,
home_county VARCHAR(75) not null,
home_state VARCHAR(20) not null,
home_zip VARCHAR(20) not null,
act VARCHAR(20)  null
);

//This table was populated by population-by-county.csv (included in this zip) which I downloaded //from http://census.ire.org/data/bulkdata.html

CREATE TABLE population_by_county (
id BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
county_id MEDIUMINT not null,
county_name VARCHAR(75) not null,
population BIGINT(20) not null
);

Then in my main class I get the list of all Iowa counties (trivia: there are 99 counties :)) and their popoulation from table population_by_county. I loop through each county and then query student_year_2013 table to get the student count for that county. I store this county in an List of county info called data. I also calculate per capital enrollment and add to the same list. While looping through the data list I print them out to the console.

Interesting find: county that has the highest per capital enrollment at University of Iowa is Johnson County where lies Iowa City itself. 