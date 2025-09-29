from http.server import HTTPServer, BaseHTTPRequestHandler
import urllib.parse
from datetime import datetime
import json

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
        
        print("\n" + "="*50)
        print(f"📱 POST RECEIVED - {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print("="*50)
        print(f"Full data: {sms_text}")
        print(f"Headers: {dict(self.headers)}")
        print("="*50)
        
        # Send response back to Android app
        self.send_response(200)
        self.send_header('Content-type', 'text/plain')
        self.end_headers()
        response_message = f"Server: POST received - {sms_text}"
        self.wfile.write(response_message.encode('utf-8'))
        
        # Save to file
        self.save_to_file(sms_text)
    
    def do_GET(self):
        # Handle GET requests (for testing)
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        
        html = """
        <html>
        <head><title>SMS Receiver Server</title></head>
        <body>
            <h1>📱 SMS Receiver Server is Running!</h1>
            <p>Server is ready to receive POST data from Android app.</p>
            <p>Check console for incoming messages.</p>
        </body>
        </html>
        """
        self.wfile.write(html.encode('utf-8'))
    
    def save_to_file(self, data):
        """Save received data to a file"""
        try:
            with open("received_data.log", "a", encoding="utf-8") as f:
                timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
                f.write(f"{timestamp} - {data}\n")
        except Exception as e:
            print(f"Error saving to file: {e}")
    
    def log_message(self, format, *args):
        # Custom log format
        print(f"[{datetime.now().strftime('%H:%M:%S')}] {args[0]}")

def run_server(port=8000):
    server_address = ('', port)
    httpd = HTTPServer(server_address, SMSRequestHandler)
    
    print(f"🚀 Custom POST Server started!")
    print(f"📡 Listening on port {port}")
    print(f"🌐 Access via: http://localhost:{port}")
    print(f"📱 Ready to receive POST requests from Android app")
    print(f"💾 Data will be saved to 'received_data.log'")
    print("Press Ctrl+C to stop the server\n")
    
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n🛑 Server stopped")

if __name__ == '__main__':
    run_server()
