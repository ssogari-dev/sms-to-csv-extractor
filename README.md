
# SMS to CSV Extractor

## Introduction
The **SMS to CSV Extractor** is an Android application designed to export SMS and MMS messages into a CSV format for easier management and processing. This application is particularly useful for collecting AI training datasets of SMS or for data backup purposes.

## Features
- **Period Selection**: Choose the timeframe for SMS/MMS to be exported.
- **Type Selection**: Select either SMS or MMS, and whether to include received (incoming) or sent (outgoing) messages.
- **Customizable Export**: Choose and rearrange the data fields to be included in the CSV file.
- **Extracted File Sharing**: After exporting the CSV file, the app automatically activates the sharing dialog to allow immediate file transfer.

## CSV Fields Description
- **Type**: Displays the type of message, either SMS or MMS.
- **Receiver Number**: Shows the receiver's number, which may include the country code or display as 'Me' based on device settings.
- **Sender Number**: Displays the sender's number, which may include the country code or display as 'Me' based on device settings.
- **Direction**: Indicates whether the message is outgoing (sent) or incoming (received).
- **Date**: Shows the date in yyyy-MM-dd format.
- **Time**: Shows the time in HH:mm:ss format (ss may display as 00 depending on device settings).
- **Datetime**: Displays both date and time in yyyy-MM-dd HH:mm:ss format.
- **Title**: Displays the title of the message for MMS.
- **Body**: Contains the content of the SMS/MMS.

## Usage
1. **Select Time Period**: Choose the period for which you want to export messages. Selecting "ALL" will export all messages stored on the device.
2. **Select Message Type**: Choose between SMS and MMS, and specify whether you want to export incoming, outgoing, or both types of messages.
3. **Customize CSV Fields**: By default, the CSV will include Type, Sender Number, Datetime, and Body. You can drag and drop to reorder these fields or add additional data such as Receiver Number, Direction, Date, Time, Title (for MMS), etc.
4. **Export**: Tap the export button to save the CSV file. The default filename format is `sms_backup_yyyyMMddHHmm.csv`, but you can change the filename and save location.
5. **Share**: After saving, you can share the CSV file through various applications.

## Important Notes
- If you encounter encoding issues with MS Office Excel, use the [Data] menu, select [Get Data] -> [Text/CSV], and set the file origin/encoding to Unicode (UTF-8).
- This app does not support RCS (Rich Communication Services) messages like those from Samsung's 'Chat+' or Google's 'Chat'. Future updates may add support if APIs become available.
- If you find any bugs or have suggestions, please contact us via email(admin at ssogari dot dev) or Twitter @ssogari_dev. Contributions via GitHub are also welcome.

## Download
- **APK File**: [app-release.apk](https://github.com/ssogari-dev/sms-to-csv-extractor/releases/tag/apk-release)
- **VirusTotal Report**: [VirusTotal Report](https://www.virustotal.com/gui/file/f1765d684ce732dec221897578b72c13e0611ba11cb6e4c05f883a7decfddbeb/detection)

## Version Information
- **Version**: 2.1
- **SHA-256**: f1765d684ce732dec221897578b72c13e0611ba11cb6e4c05f883a7decfddbeb
- **MD5**: 0f596678beabcb51d4a94a1435a068b6

---

# SMS to CSV Extractor

## 소개
**SMS to CSV Extractor**는 SMS와 MMS 메시지를 CSV 형식으로 내보낼 수 있도록 한 안드로이드(Android) 애플리케이션입니다. 이 앱은 인공지능 학습용 SMS 데이터셋 수집이나 데이터 백업 등의 목적으로 특히 유용합니다.

## 기능
- **기간 선택**: 내보낼 SMS/MMS의 기간을 선택할 수 있습니다.
- **유형 선택**: SMS 또는 MMS를 선택하고, 수신(받은 메시지) 또는 발신(보낸 메시지) 메시지를 포함할지 선택할 수 있습니다.
- **내보내기 맞춤화**: CSV 파일에 포함할 데이터 필드를 선택하고 순서를 재배치할 수 있습니다.
- **추출된 파일 공유**: CSV 파일을 내보낸 후 자동으로 공유 다이얼로그를 활성화하여 파일을 바로 전송할 수 있도록 하였습니다.

## CSV 필드 설명
- **유형 (Type)**: SMS 또는 MMS 메시지 유형을 표시합니다.
- **수신자 번호 (Receiver Number)**: 수신자의 번호를 표시하며, 기기 설정에 따라 국가 번호를 포함하거나 'Me'로 표시될 수 있습니다.
- **발신자 번호 (Sender Number)**: 발신자의 번호를 표시하며, 기기 설정에 따라 국가 번호를 포함하거나 'Me'로 표시될 수 있습니다.
- **방향 (Direction)**: 발신(보낸 메시지) 또는 수신(받은 메시지) 여부를 표시합니다.
- **날짜 (Date)**: yyyy-MM-dd 형식으로 날짜를 표시합니다.
- **시간 (Time)**: HH:mm:ss 형식으로 시간을 표시하며, 기기 설정에 따라 ss는 00으로 표시될 수 있습니다.
- **일시 (Datetime)**: yyyy-MM-dd HH:mm:ss 형식으로 날짜와 시간을 표시합니다.
- **제목 (Title)**: MMS의 경우 메시지 제목을 표시합니다.
- **내용 (Body)**: SMS/MMS의 내용을 표시합니다.

## 사용법
1. **기간 선택**: 내보낼 메시지의 기간을 선택합니다. '전체'를 선택하면 기기에 저장된 모든 메시지를 내보낼 수 있습니다.
2. **메시지 유형 선택**: SMS와 MMS 중 선택하고, 수신/발신 메시지를 선택합니다.
3. **CSV 필드 맞춤화**: 기본적으로 CSV에는 유형(Type), 발신자 번호(Sender Number), 일시(Datetime), 내용(Body)이 포함됩니다. 필드의 순서를 변경하거나 추가적인 데이터를 추가할 수 있습니다.
4. **내보내기**: 내보내기 버튼을 눌러 CSV 파일을 저장합니다. 기본 파일명 형식은 `sms_backup_yyyyMMddHHmm.csv`이며, 사용자가 직접 파일명과 저장 경로를 변경할 수 있습니다.
5. **공유**: 저장 후에는 다양한 애플리케이션을 통해 CSV 파일을 공유할 수 있습니다.

## 중요 참고 사항
- Excel에서 인코딩 문제가 발생하는 경우, [데이터] 메뉴에서 [데이터 가져오기] -> [텍스트/CSV]를 선택하고 파일 원본/인코딩을 유니코드(UTF-8)로 설정하십시오.
- 삼성 '채팅+' 또는 구글 '채팅'과 같은 RCS(Rich Communication Services) 메시지는 지원하지 않습니다. 추후 API 공개 시 지원이 추가될 수 있습니다.
- 버그를 발견하거나 제안 사항이 있으시면 이메일(admin at ssogari dot dev)이나 트위터(@ssogari_dev)로 연락주시기 바랍니다. GitHub를 통한 기여도 환영합니다.

## 다운로드
- **APK 파일**: [app-release.apk](https://github.com/ssogari-dev/sms-to-csv-extractor/releases/tag/apk-release)
- **VirusTotal 보고서**: [VirusTotal Report](https://www.virustotal.com/gui/file/f1765d684ce732dec221897578b72c13e0611ba11cb6e4c05f883a7decfddbeb/detection)

## 버전 정보
- **버전**: 2.1
- **SHA-256**: f1765d684ce732dec221897578b72c13e0611ba11cb6e4c05f883a7decfddbeb
- **MD5**: 0f596678beabcb51d4a94a1435a068b6
