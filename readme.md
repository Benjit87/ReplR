Wrap around R with Java and uses ZeroMQ for communication
Used this because i am unable to compile ZeroMq to Windows
If u are looking for something similar you should see 
IPython R kernel https://github.com/takluyver/IRkerne

Make sure JRI package is installed 

Execute with the following below

EXPORT R_HOME=/usr/lib/R                      
java -Djava.library.path=/usr/local/lib/R/si-library/rJava/jri -jar ReplR-all-1.0.jar
              
