# Lofty Android Security

This malicious Android application is an educational exercise to refresh my understanding of Android architecture — interactions between Activities, Services, BroadcastReceivers, and ContentProviders — and to study how insecure implementations can lead to data exposure. The app simulates data exfiltration to a controlled test server for research purposes.

https://github.com/user-attachments/assets/f1d7ee3b-1302-48ee-85db-73342114731b

### Main features of the application are

- Device informaation
  
- Get user fine course location
  
- Monitor messaging
  
- More features coming
### Pre-requisites
1. Run your attackers Server
  `python3 lofty-server.py`
  Or
  `python3 lofty-server1.py`

2. Change the Server Ip on the source Code.Just do a search and replace of `http://192.168.100.39:8000/post` and replace that to your ip.
<img width="455" height="137" alt="lofty-ip" src="https://github.com/user-attachments/assets/4519a813-b711-4d26-9ee8-2fb17697f3bd" />

3.  
### Build from source

1. Set Android studio and clone repo
   
   `git clone https://github.com/B3nac/InjuredAndroid.git`

2. Run the application.
   - Many ways to run via adb,emulator etc

NB: **<u>*I recommend you to remove the server files to another folder</u>.***

### Contribution

Contributions are welcome — let’s keep learning.  
Please open issues for bugs or suggestions and send pull requests for changes; keep PRs focused and include a short description of the change. Be respectful and avoid adding active payloads, real credentials, or anything unsafe.

Disclaimer

> This repository contains a **malicious**-behaviour simulation intended **only** for controlled, ethical research and education. Use of this code on devices, networks, or systems you do **not** own or do not have **explicit, written permission** to test is unlawful and unethical.
> 
> By using this code you agree to:
> 
> - Run it only in isolated, legal test environments (emulators, lab devices, virtual machines, or dedicated test networks).
>   
> - Never deploy it against third-party devices, public services, or production systems.
>   
> - Remove or neutralize any active payloads before sharing or publishing binary builds.
>   
> 
> The author and contributors are **not** responsible for misuse. If you are unsure whether your planned use is permitted, obtain written authorization from the system owner or do not proceed.

