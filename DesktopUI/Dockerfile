# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-alpine

# Install X11 and other dependencies for GUI
RUN apk add --no-cache \
    xvfb \
    libxtst \
    libxi \
    libxrender

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/DesktopUI-1.0-SNAPSHOT.jar app.jar

# Set up X11 forwarding (for Linux/macOS)
ENV DISPLAY=host.docker.internal:0

# Run the application using Xvfb (virtual display)
CMD xvfb-run --server-args="-screen 0 1024x768x24" java -cp app.jar org.swingUI.Main