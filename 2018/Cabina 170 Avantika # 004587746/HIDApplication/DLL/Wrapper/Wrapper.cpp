/* Author:	SYLAM
 * Date:	15 July 2007
 * 
 * Description:	Wrapping the Win32 API for Java
 * 
 * Acknowlegdement: www.lvr.com
 * 
 * 
*/


// Wrapper.cpp : Defines the entry point for the DLL application.
//
#include "stdafx.h"
#include <windows.h>
#include "MyHID.h"

extern "C" {

// This file is in the Windows DDK available from Microsoft.
#include "hidsdi.h"

#include <setupapi.h>
#include <dbt.h>
}

#ifdef _MANAGED
#pragma managed(push, off)
#endif

HANDLE		DeviceHandle = INVALID_HANDLE_VALUE;
HANDLE		ReadHandle = INVALID_HANDLE_VALUE;
HANDLE		WriteHandle = INVALID_HANDLE_VALUE;
HIDP_CAPS	Capabilities;
WCHAR*		MyDevicePathName;

void GetDeviceCapabilities();


BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
    return TRUE;
}

JNIEXPORT jint JNICALL Java_MyHID_ReadFeatureReportSize(JNIEnv *jEnv, jobject jObj)
{
	if (DeviceHandle == INVALID_HANDLE_VALUE) return 0;
	else return (Capabilities.FeatureReportByteLength);
}

JNIEXPORT jint JNICALL Java_MyHID_ReadInputReportSize(JNIEnv *jEnv, jobject jObj)
{
	if (DeviceHandle == INVALID_HANDLE_VALUE) return 0;
	else return (Capabilities.InputReportByteLength);
}

JNIEXPORT jint JNICALL Java_MyHID_ReadOutputReportSize(JNIEnv *jEnv, jobject jObj)
{
	if (DeviceHandle == INVALID_HANDLE_VALUE) return 0;
	else return (Capabilities.OutputReportByteLength);
}

JNIEXPORT jbyteArray JNICALL Java_MyHID_ReadFeatureReport(JNIEnv *jEnv, jobject jObj)
{
	byte *buffer;
	
	if (DeviceHandle == INVALID_HANDLE_VALUE) return jEnv->NewByteArray(0);

	buffer = new byte[Capabilities.FeatureReportByteLength];
	
	if (!HidD_GetFeature
			(DeviceHandle,
			buffer,
			Capabilities.FeatureReportByteLength))
	{
		delete buffer;
		return jEnv->NewByteArray(0);
	}

	jbyteArray featureReport = jEnv->NewByteArray(Capabilities.FeatureReportByteLength);
	jEnv->SetByteArrayRegion(featureReport, 0, Capabilities.FeatureReportByteLength, (jbyte*)buffer);
	delete buffer;
	return featureReport;
}

JNIEXPORT jbyteArray JNICALL Java_MyHID_IntReadInputReport(JNIEnv *jEnv, jobject jObj)
{
	byte *buffer;
	
	if (ReadHandle == INVALID_HANDLE_VALUE) return jEnv->NewByteArray(0);

	buffer = new byte[Capabilities.InputReportByteLength];

	/*API call:ReadFile
	'Returns: the report in InputReport.
	'Requires: a device handle returned by CreateFile
	'(for overlapped I/O, CreateFile must be called with FILE_FLAG_OVERLAPPED),
	'the Input report length in bytes returned by HidP_GetCaps,
	'and an overlapped structure whose hEvent member is set to an event object.
	*/

	DWORD		NumberOfBytesRead;
	if (!ReadFile(ReadHandle, buffer, Capabilities.InputReportByteLength,
					&NumberOfBytesRead,	NULL))
	{
		delete buffer;
		return jEnv->NewByteArray(0);
	}

	jbyteArray inputReport = jEnv->NewByteArray(Capabilities.InputReportByteLength);
	jEnv->SetByteArrayRegion(inputReport, 0, Capabilities.InputReportByteLength, (jbyte*)buffer);
	delete buffer;
	return inputReport;

}

JNIEXPORT jbyteArray JNICALL Java_MyHID_CtrlReadInputReport(JNIEnv *jEnv, jobject jObj)
{
	byte *buffer;
	
	if (DeviceHandle == INVALID_HANDLE_VALUE) return jEnv->NewByteArray(0);

	buffer = new byte[Capabilities.InputReportByteLength];

	//Read a report from the device using a control transfer.

	/*
	HidD_GetInputReport
	Returns:
	True on success
	Requires: 
	A device handle returned by CreateFile.
	A buffer to hold the report.
	The report length returned by HidP_GetCaps in Capabilities.InputReportByteLength.
	*/
	
	if (!HidD_GetInputReport(DeviceHandle, buffer, Capabilities.InputReportByteLength))
	{
		delete buffer;
		return jEnv->NewByteArray(0);
	}

	jbyteArray inputReport = jEnv->NewByteArray(Capabilities.InputReportByteLength);
	jEnv->SetByteArrayRegion(inputReport, 0, Capabilities.InputReportByteLength, (jbyte*)buffer);
	delete buffer;
	return inputReport;

}

JNIEXPORT jboolean JNICALL Java_MyHID_SendFeatureReport(JNIEnv *jEnv, jobject jObj, jbyteArray featureReport)
{
	boolean Result = false;
	byte *buffer = new byte[Capabilities.FeatureReportByteLength];
	jboolean isCopy = JNI_TRUE;
	buffer = (byte*)jEnv->GetByteArrayElements(featureReport, &isCopy);

	//Send a report to the device.

	/*
	HidD_SetFeature
	Sends a report to the device.
	Returns: success or failure.
	Requires:
	A device handle returned by CreateFile.
	A buffer that holds the report.
	The Output Report length returned by HidP_GetCaps,
	*/

	if (DeviceHandle != INVALID_HANDLE_VALUE) Result = HidD_SetFeature
														(DeviceHandle,
														buffer,
														Capabilities.FeatureReportByteLength);
	delete buffer;
	return Result;
}

JNIEXPORT jboolean JNICALL Java_MyHID_IntSendOutputReport(JNIEnv *jEnv, jobject jObj, jbyteArray outputReport)
{
	boolean Result = false;
	byte *buffer = new byte[Capabilities.OutputReportByteLength];
	jboolean isCopy = JNI_TRUE;
	buffer = (byte*)jEnv->GetByteArrayElements(outputReport, &isCopy);

	/*
	API Function: WriteFile
	Sends a report to the device.
	Returns: success or failure.
	Requires:
	A device handle returned by CreateFile.
	A buffer that holds the report.
	The Output Report length returned by HidP_GetCaps,
	A variable to hold the number of bytes written.
	*/

	DWORD BytesWritten = 0;
	if (WriteHandle != INVALID_HANDLE_VALUE) Result = WriteFile
														(WriteHandle, 
														buffer, 
														Capabilities.OutputReportByteLength, 
														&BytesWritten, 
														NULL);

	delete buffer;
	return Result;
}

JNIEXPORT jboolean JNICALL Java_MyHID_CtrlSendOutputReport(JNIEnv *jEnv, jobject jObj, jbyteArray outputReport)
{
	boolean Result = false;
	byte *buffer = new byte[Capabilities.OutputReportByteLength];
	jboolean isCopy = JNI_TRUE;
	buffer = (byte*)jEnv->GetByteArrayElements(outputReport, &isCopy);

	//Send a report to the device.

	/*
	HidD_SetOutputReport
	Sends a report to the device.
	Returns: success or failure.
	Requires:
	The device handle returned by CreateFile.
	A buffer that holds the report.
	The Output Report length returned by HidP_GetCaps,
	*/

	DWORD BytesWritten = 0;
	if (WriteHandle != INVALID_HANDLE_VALUE) Result = HidD_SetOutputReport
														(WriteHandle,
														buffer,
														Capabilities.OutputReportByteLength);

	delete buffer;
	return Result;
}

JNIEXPORT jboolean JNICALL Java_MyHID_FindTheHID(JNIEnv *jEnv, jobject jObj, jint vendorID, jint productID)
{
		//Use a series of API calls to find a HID with a specified Vendor IF and Product ID.

	HIDD_ATTRIBUTES				Attributes;
	SP_DEVICE_INTERFACE_DATA	devInfoData;
	int							MemberIndex = 0;
	LONG						Result;	
	DWORD						Required;
	DWORD						Length = 0;
	PSP_DEVICE_INTERFACE_DETAIL_DATA	detailData = NULL;
	GUID						HidGuid;
	HDEVINFO					hDevInfo;
	jboolean					MyDeviceDetected;

	if (DeviceHandle != INVALID_HANDLE_VALUE)
	{
		CloseHandle(DeviceHandle);
		DeviceHandle = INVALID_HANDLE_VALUE;
	}
	if (WriteHandle != INVALID_HANDLE_VALUE)
	{
		CloseHandle(WriteHandle);
		WriteHandle = INVALID_HANDLE_VALUE;
	}

	/*
	API function: HidD_GetHidGuid
	Get the GUID for all system HIDs.
	Returns: the GUID in HidGuid.
	*/

	HidD_GetHidGuid(&HidGuid);	
	
	/*
	API function: SetupDiGetClassDevs
	Returns: a handle to a device information set for all installed devices.
	Requires: the GUID returned by GetHidGuid.
	*/
	
	hDevInfo=SetupDiGetClassDevs 
		(&HidGuid, 
		NULL, 
		NULL, 
		DIGCF_PRESENT|DIGCF_INTERFACEDEVICE);


	devInfoData.cbSize = sizeof(devInfoData);

	//Step through the available devices looking for the one we want. 
	//Quit on detecting the desired device or checking all available devices without success.

	MemberIndex = 0;
	MyDeviceDetected = FALSE;

	while ((SetupDiEnumDeviceInterfaces 
			(hDevInfo, 
			0, 
			&HidGuid, 
			MemberIndex++, 
			&devInfoData))&&(MyDeviceDetected==false))
	{
		/*
		API function: SetupDiEnumDeviceInterfaces
		On return, MyDeviceInterfaceData contains the handle to a
		SP_DEVICE_INTERFACE_DATA structure for a detected device.
		Requires:
		The DeviceInfoSet returned in SetupDiGetClassDevs.
		The HidGuid returned in GetHidGuid.
		An index to specify a device.
		*/

		//A device has been detected, so get more information about it.

		/*
		API function: SetupDiGetDeviceInterfaceDetail
		Returns: an SP_DEVICE_INTERFACE_DETAIL_DATA structure
		containing information about a device.
		To retrieve the information, call this function twice.
		The first time returns the size of the structure in Length.
		The second time returns a pointer to the data in DeviceInfoSet.
		Requires:
		A DeviceInfoSet returned by SetupDiGetClassDevs
		The SP_DEVICE_INTERFACE_DATA structure returned by SetupDiEnumDeviceInterfaces.
		
		The final parameter is an optional pointer to an SP_DEV_INFO_DATA structure.
		This application doesn't retrieve or use the structure.			
		If retrieving the structure, set 
		MyDeviceInfoData.cbSize = length of MyDeviceInfoData.
		and pass the structure's address.
		*/
		
		//Get the Length value.
		//The call will return with a "buffer too small" error which can be ignored.

		Result = SetupDiGetDeviceInterfaceDetail 
			(hDevInfo, 
			&devInfoData, 
			NULL, 
			0, 
			&Length, 
			NULL);

		//Allocate memory for the hDevInfo structure, using the returned Length.

		detailData = (PSP_DEVICE_INTERFACE_DETAIL_DATA)malloc(Length);
		
		//Set cbSize in the detailData structure.

		detailData -> cbSize = sizeof(SP_DEVICE_INTERFACE_DETAIL_DATA);

		//Call the function again, this time passing it the returned buffer size.

		Result = SetupDiGetDeviceInterfaceDetail 
			(hDevInfo, 
			&devInfoData, 
			detailData, 
			Length, 
			&Required, 
			NULL);

		// Open a handle to the device.
		// To enable retrieving information about a system mouse or keyboard,
		// don't request Read or Write access for this handle.

		/*
		API function: CreateFile
		Returns: a handle that enables reading and writing to the device.
		Requires:
		The DevicePath in the detailData structure
		returned by SetupDiGetDeviceInterfaceDetail.
		*/

		DeviceHandle=CreateFile 
			(detailData->DevicePath, 
			0, 
			FILE_SHARE_READ|FILE_SHARE_WRITE, 
			(LPSECURITY_ATTRIBUTES)NULL,
			OPEN_EXISTING, 
			0, 
			NULL);

		/*
		API function: HidD_GetAttributes
		Requests information from the device.
		Requires: the handle returned by CreateFile.
		Returns: a HIDD_ATTRIBUTES structure containing
		the Vendor ID, Product ID, and Product Version Number.
		Use this information to decide if the detected device is
		the one we're looking for.
		*/

		//Set the Size to the number of bytes in the structure.

		Attributes.Size = sizeof(Attributes);

		Result = HidD_GetAttributes 
			(DeviceHandle, 
			&Attributes);
		
		if ((Attributes.VendorID == vendorID)&&(Attributes.ProductID == productID))
		{
			//Both the Vendor ID and Product ID match.

			MyDeviceDetected = TRUE;
			MyDevicePathName = detailData->DevicePath;

			//Get the device's capablities.

			GetDeviceCapabilities();

			// Get a handle for writing Output reports.

			WriteHandle=CreateFile 
				(detailData->DevicePath, 
				GENERIC_WRITE, 
				FILE_SHARE_READ|FILE_SHARE_WRITE, 
				(LPSECURITY_ATTRIBUTES)NULL,
				OPEN_EXISTING, 
				0, 
				NULL);
	
			// Get a handle for writing Output reports.

			ReadHandle=CreateFile 
				(detailData->DevicePath, 
				GENERIC_READ, 
				FILE_SHARE_READ|FILE_SHARE_WRITE,
				(LPSECURITY_ATTRIBUTES)NULL, 
				OPEN_EXISTING, 
				0, 
				NULL);

		} //if (Attributes.VendorID == VendorID)

		else
			//The Vendor ID doesn't match.

			CloseHandle(DeviceHandle);

		//Free the memory used by the detailData structure (no longer needed).

		free(detailData);
	}


	//Free the memory reserved for hDevInfo by SetupDiClassDevs.

	SetupDiDestroyDeviceInfoList(hDevInfo);

/*	jclass clazz = jEnv->GetObjectClass(jObj);
	jfieldID fid = jEnv->GetFieldID(clazz, "readHandle", "I");
	jEnv->SetIntField(jObj, fid, (jint)DeviceHandle);
	fid = jEnv->GetFieldID(clazz, "writeHandle", "I");
	jEnv->SetIntField(jObj, fid, (jint)WriteHandle);
	fid = jEnv->GetFieldID(clazz, "inputReportLength", "I");
	jEnv->SetIntField(jObj, fid, Capabilities.InputReportByteLength);
	fid = jEnv->GetFieldID(clazz, "outputReportLength", "I");
	jEnv->SetIntField(jObj, fid, Capabilities.OutputReportByteLength);
*/
	if (!MyDeviceDetected)
	{
		DeviceHandle = INVALID_HANDLE_VALUE;
		ReadHandle = INVALID_HANDLE_VALUE;
		WriteHandle = INVALID_HANDLE_VALUE;
	}

	return MyDeviceDetected;
}

JNIEXPORT void JNICALL Java_MyHID_CloseHandles(JNIEnv *jEnv, jobject jObj)
{
	if (DeviceHandle != INVALID_HANDLE_VALUE)
	{
		CloseHandle(DeviceHandle);
		DeviceHandle = INVALID_HANDLE_VALUE;
	}
	if (ReadHandle != INVALID_HANDLE_VALUE)
	{
		CloseHandle(ReadHandle);
		ReadHandle = INVALID_HANDLE_VALUE;
	}
	if (WriteHandle != INVALID_HANDLE_VALUE)
	{
		CloseHandle(WriteHandle);
		WriteHandle = INVALID_HANDLE_VALUE;
	}
}

void GetDeviceCapabilities()
{
	//Get the Capabilities structure for the device.

	PHIDP_PREPARSED_DATA	PreparsedData;

	/*
	API function: HidD_GetPreparsedData
	Returns: a pointer to a buffer containing the information about the device's capabilities.
	Requires: A handle returned by CreateFile.
	There's no need to access the buffer directly,
	but HidP_GetCaps and other API functions require a pointer to the buffer.
	*/

	HidD_GetPreparsedData 
		(DeviceHandle, 
		&PreparsedData);

	/*
	API function: HidP_GetCaps
	Learn the device's capabilities.
	For standard devices such as joysticks, you can find out the specific
	capabilities of the device.
	For a custom device, the software will probably know what the device is capable of,
	and the call only verifies the information.
	Requires: the pointer to the buffer returned by HidD_GetPreparsedData.
	Returns: a Capabilities structure containing the information.
	*/
	
	HidP_GetCaps 
		(PreparsedData, 
		&Capabilities);

	//No need for PreparsedData any more, so free the memory it's using.

	HidD_FreePreparsedData(PreparsedData);
}

#ifdef _MANAGED
#pragma managed(pop)
#endif

