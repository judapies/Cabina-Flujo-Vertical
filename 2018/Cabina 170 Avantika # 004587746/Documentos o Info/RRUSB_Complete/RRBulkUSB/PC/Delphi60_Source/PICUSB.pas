///////////////////////////////////////////////////////////////////////////////////////////////////
//
//   Ejemplo de comunicaciones PIC <-> PC v?a USB 2.0
//
//   by Redraven http://picmania.garcia-cuervo.com
//
///////////////////////////////////////////////////////////////////////////////////////////////////

unit PICUSB;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, usbAPI, ExtCtrls, shellapi,
  ComCtrls, jpeg, Buttons;
Const
    vid_pid  : PCHAR  = 'vid_04d8&pid_000b'+#0;
    out_pipe : PCHAR = '\MCHP_EP1' + #0;
    in_pipe  : PCHAR = '\MCHP_EP1' + #0;


    TOGGLE_LED        = $01;
    GET_FIRMWARE      = $02;
    SEND_STRING_RS232 = $03;

    UsbBufSize = 32;

Type
    PByteBuffer = ^TByteBuffer;
    TByteBuffer = Array[0..63] of Byte;
    PUsbData    = ^TUsbData;
    TUSBData    = Record
       Cmd:       Byte;
       Data:      Array[0..UsbBufSize-1] of Byte;
    End;
    TVersionInfo = Array[0..3] of Byte;



    TfrmUsbMonitor = class(TForm)
    Memo1: TMemo;
    btnGetUSBDriverVersion: TButton;
    btnGetUSBSummary: TButton;
    StatusBar1: TStatusBar;
    Bevel1: TBevel;
    Label1: TLabel;
    Label2: TLabel;
    btnGetFirmwareVersion: TButton;
    btnClearDump: TButton;
    Panel_Foto_Outer: TPanel;
    Panel_Foto_Inner: TPanel;
    Image1: TImage;
    EditRS232: TEdit;
    btnSendToRS232: TButton;
    Bevel4: TBevel;
    Label4: TLabel;
    btnLedMonitor: TButton;
    BtnCerrar: TBitBtn;
    Bevel2: TBevel;
    Bevel3: TBevel;
    Bevel5: TBevel;
    procedure FormCreate(Sender: TObject);
    procedure btnGetUSBDriverVersionClick(Sender: TObject);
    procedure btnGetUSBSummaryClick(Sender: TObject);
    procedure btnLedMonitorClick(Sender: TObject);
    procedure btnGetFirmwareVersionClick(Sender: TObject);
    procedure btnClearDumpClick(Sender: TObject);
    procedure btnSendToRS232Click(Sender: TObject);
    procedure BtnCerrarClick(Sender: TObject);
  private
  public
    myOutPipe: THANDLE;
    myInPipe:  THANDLE;
    function   USBCheckInvalidHandle():string;
    function   USBSendReceivePacket(SendData: PByteBuffer; SendLength: DWORD; ReceiveData: PByteBuffer; var ReceiveLength: DWORD; SendDelay: Word; ReceiveDelay:Word):DWORD;
    function   GetUSBSummary():Integer;
    function   GetUSBDriverVersion(): TVersionInfo;
    function   SendUSBSimpleCommand(Command: byte; Title: string): string;
    function   SendUSBParamCommand(Command: byte; Param: string; Title: string): string;
    function   GetUSBRequest(Command: byte; ReceiveLength: DWORD; Title: string; Prefix: string): string;
  end;

var
  frmUsbMonitor: TfrmUsbMonitor;

implementation

{$R *.dfm}

///////////////////////////////////////////////////////////////////////////////////////////////////
//
// Funciones de comuniaciones USB
//
///////////////////////////////////////////////////////////////////////////////////////////////////

function TfrmUsbMonitor.USBCheckInvalidHandle():string;
var
   res: string;
Begin
  if (GetLastError() = ERROR_INVALID_HANDLE) then
  Begin
    MPUSBClose(myOutPipe);
    MPUSBClose(myInPipe);
    myOutPipe := INVALID_HANDLE_VALUE;
    myInPipe  := INVALID_HANDLE_VALUE;
    res := 'USB INVALID_HANDLE_VALUE';
  End
  else
    res := 'Error Code ' + IntToStr(GetLastError());
  result := res;
End;

Function TfrmUsbMonitor.USBSendReceivePacket(SendData: PByteBuffer; SendLength: DWORD; ReceiveData: PByteBuffer; var ReceiveLength: DWORD; SendDelay: Word; ReceiveDelay:Word):DWORD;
var
   SentDataLength:  DWORD ;
   ExpectedReceiveLength: DWORD;
Begin
     ExpectedReceiveLength := ReceiveLength;
     if(myOutPipe <> INVALID_HANDLE_VALUE) and ( myInPipe <> INVALID_HANDLE_VALUE) then
     Begin
          if MPUSBWrite(myOutPipe,SendData,SendLength,@SentDataLength,SendDelay) <> 0 then
          Begin
               if(MPUSBRead(myInPipe,ReceiveData,ExpectedReceiveLength,@ReceiveLength,ReceiveDelay)) <> 0 then
               Begin
                    if (ReceiveLength = ExpectedReceiveLength) Then
                    Begin
                         result:=1;
                         Exit;
                    End
                    else
                    Begin
                         if (ReceiveLength < ExpectedReceiveLength) then
                         begin
                              result:=2;
                              Exit;
                         End;
                    End
               End
               else
               Memo1.Lines.Add(USBCheckInvalidHandle());
          End
          else
              Memo1.Lines.Add(USBCheckInvalidHandle());
     End;
     result:=0;
End;

function TfrmUsbMonitor.GetUSBSummary():Integer;
Var
   tempPipe:THandle;
   count:DWORD;
   max_Count:DWORD;
   i:Byte;
Begin
     max_count := MPUSBGetDeviceCount(vid_pid);
     if(max_count=0) then
     Begin
          result:= max_count;
          Memo1.Lines.add('No device found');
          exit;
     End
     Else
         memo1.lines.add(IntToStr(max_Count) + ' device(s) with ' + vid_pid  +  ' currently attached');
     count := 0;
     For i:=0 to MAX_NUM_MPUSB_DEV-1 Do
     Begin
          tempPipe := MPUSBOpen(i,vid_pid,NIL,MP_READ,0);
          if(tempPipe <> INVALID_HANDLE_VALUE) then
          Begin
               memo1.lines.add('Instance Index ' + IntToStr(i));
               MPUSBClose(tempPipe);
               Inc(count);
          end;
          if(count = max_count) Then break;
     end;
     result:= max_count;
End;

function TfrmUsbMonitor.GetUSBDriverVersion(): TVersionInfo;
var
   temp:DWORD;
   VersionInfo: TVersionInfo;
begin
  temp := MPUSBGetDLLVersion();
  move(temp,VersionInfo,sizeof(VersionInfo));
  result := VersionInfo;
end;

function TfrmUsbMonitor.GetUSBRequest(Command: byte; ReceiveLength: DWORD; Title: string; Prefix: string): string;
Var
   Selection:   DWORD;
   RecvLength:  DWORD;
   send_buf:    TByteBuffer;
   receive_buf: TByteBuffer;
   p: array[0..UsbBufSize-1] of char;
   i: integer;
   s: string;
Begin
    Selection :=0;
    myOutPipe := MPUSBOpen(selection,vid_pid, out_pipe, MP_WRITE, 0);
    myInPipe  := MPUSBOpen(selection,vid_pid, in_pipe,  MP_READ,  0);
    If (myOutPipe = INVALID_HANDLE_VALUE) or (myInPipe = INVALID_HANDLE_VALUE) then
    Begin
      s := 'USB Failed to open data pipes.';
      Exit;
    End;
    send_buf[0] := Command;
    RecvLength  := ReceiveLength;
    if (USBSendReceivePacket(@send_buf,1,@receive_buf,RecvLength,1000,1000) = 1) Then
    Begin
         for i:=0 to ReceiveLength do p[i] :=Chr(receive_buf[i]);
         s := Prefix+strpas(p);
    End
    Else
      s := 'USB Operation Failed : '+Title;
    MPUSBClose(myOutPipe);
    MPUSBClose(myInPipe);
    myOutPipe := INVALID_HANDLE_VALUE;
    myInPipe := INVALID_HANDLE_VALUE;
    result := s;
end;

function TfrmUsbMonitor.SendUSBSimpleCommand(Command: byte; Title: string): string;
var
   selection: DWORD;
   send_buf: TUsbData;
   SentDataLength: DWORD;
   s: string;
Begin
  Selection:=0;
  myOutPipe := MPUSBOpen(selection, vid_pid, out_pipe, MP_WRITE, 0);
  if (myOutPipe = INVALID_HANDLE_VALUE) then
  Begin
    s := 'USB Failed to open out data pipe';
    Exit;
  End;
  send_buf.Cmd := Command;
  if MPUSBWrite(myOutPipe,@Send_buf,1,@SentDataLength,1000) <> 0 then
  Begin
    if (SentDataLength <> 1) Then
      s := 'USB Failure on sending : '+Title
    else
      s := Title+' : sended Ok'
  End
  else
    s := USBCheckInvalidHandle();
  MPUSBClose(myOutPipe);
  myOutPipe := INVALID_HANDLE_VALUE;
  result := s;
end;

function TfrmUsbMonitor.SendUSBParamCommand(Command: byte; Param: string; Title: string): string;
var
   selection: DWORD;
   send_buf: TUsbData;
   SentDataLength,CompareSentDataLength: DWORD;
   tmp: array[0..UsbBufSize-1] of char;
   i: integer;
   s: string;
Begin
  Selection:=0;
  myOutPipe := MPUSBOpen(selection, vid_pid, out_pipe, MP_WRITE, 0);
  if (myOutPipe = INVALID_HANDLE_VALUE) then
  Begin
    s := 'USB Failed to open out data pipe';
    Exit;
  End;
  send_buf.Cmd := Command;
  StrPCopy(tmp,Param);
  CompareSentDataLength := Length(Param);
  for i:=0 to CompareSentDataLength do
  begin
       send_buf.Data[i] := ord(tmp[i]);
  end;
  if MPUSBWrite(myOutPipe,@Send_buf,CompareSentDataLength+1,@SentDataLength,1000) <> 0 then
  Begin
    if (SentDataLength <> CompareSentDataLength+1) Then
      s := 'USB Failure on sending : '+Title
    else
      s := Title+' : sended Ok'
  End
  else
    s := USBCheckInvalidHandle();
  MPUSBClose(myOutPipe);
  myOutPipe := INVALID_HANDLE_VALUE;
  result := s;
end;

///////////////////////////////////////////////////////////////////////////////////////////////////
//
// De Formulario
//
///////////////////////////////////////////////////////////////////////////////////////////////////

procedure TfrmUsbMonitor.FormCreate(Sender: TObject);
begin
     myInPipe  := INVALID_HANDLE_VALUE;
     myOutPipe := INVALID_HANDLE_VALUE;
end;

procedure TfrmUsbMonitor.btnGetUSBDriverVersionClick(Sender: TObject);
var
   miVersionInfo: TVersionInfo;
begin
  miVersionInfo := GetUSBDriverVersion();
  memo1.text:='USB Driver Version '+intTOStr(miVersionInfo[0])+ '.'+intTOStr(miVersionInfo[1])+'.'+intTOStr(miVersionInfo[2])+'.'+intTOStr(miVersionInfo[3]);
end;

procedure TfrmUsbMonitor.btnGetUSBSummaryClick(Sender: TObject);
begin
     GetUSBSummary();
end;

procedure TfrmUsbMonitor.btnClearDumpClick(Sender: TObject);
begin
     Memo1.Lines.Clear;
end;

procedure TfrmUsbMonitor.btnGetFirmwareVersionClick(Sender: TObject);
Begin
     Memo1.lines.add(GetUSBRequest(GET_FIRMWARE,6,'Get Firmware','USB Firmware : '));
end;

procedure TfrmUsbMonitor.btnLedMonitorClick(Sender: TObject);
Begin
     Memo1.lines.add(SendUSBSimpleCommand(TOGGLE_LED,'Command TOGGLE LED'));
end;

procedure TfrmUsbMonitor.btnSendToRS232Click(Sender: TObject);
Begin
     Memo1.lines.add(SendUSBParamCommand(SEND_STRING_RS232,EditRS232.Text+#0,'String To RS232'));
end;

procedure TfrmUsbMonitor.BtnCerrarClick(Sender: TObject);
begin
     Close;
end;

end.

