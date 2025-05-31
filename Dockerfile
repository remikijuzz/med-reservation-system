# Używamy OpenJDK 17 na bazie obrazu slim
FROM openjdk:17-jdk-slim

# Ustawiamy katalog roboczy w kontenerze
WORKDIR /app

# Kopiujemy plik JAR wygenerowany przez Maven
# Zakładamy, że uruchomiłeś(aś) wcześniej: mvn clean package
# dzięki czemu w folderze target/ znajduje się app.jar
COPY target/med-reservation-system-0.0.1-SNAPSHOT.jar app.jar

# Exponujemy standardowy port Spring Boot (8080)
EXPOSE 8080

# Komenda uruchamiająca naszą aplikację
ENTRYPOINT ["java","-jar","/app/app.jar"]
