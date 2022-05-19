/* eslint-disable prettier/prettier */
/* eslint-disable react-native/no-inline-styles */
import React, { useEffect, useState } from 'react';

import {
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  NativeModules,
} from 'react-native';
// import { multiply, startScanCard, stopScanCard } from 'react-native-demo-libs';

const {
  startScanCard,
  stopScanCard,
  printTextLine,
  openPrinter,
  closePrinter,
  beginPrint,
  cleanCache,
  sleep,
} = NativeModules.DemoLibs;

export default function App() {
  const [result, setResult] = useState(false);
  const [isScanning, setIsScanning] = useState(false);
  const [cardNumber, setCardNumber] = useState('');
  const [cardHolderName, setCardHolderName] = useState('');
  const [cardExpireDate, setCardExpireDate] = useState('');
  const [issuerCountry, setIssuerCountry] = useState('');
  const [appLabel, setAppLabel] = useState('');
  const [serviceCode, setServiceCode] = useState('');
  const [cardSequenceNumber, setCardSequenceNumber] = useState('');
  const [error, setError] = useState();

  const handleScan = () => {
    setError();
    setResult(false);
    setIsScanning(true);
    startScanCard()
      .then((res) => {
        if (res) {
          setCardNumber(res?.cardNumber || '');
          setCardHolderName(res?.cardHolderName || '');
          setCardExpireDate(new Date(res?.cardExpireDate).toDateString());
          setIssuerCountry(res?.issuerCountryCode || '');
          setAppLabel(res?.appLabel || '');
          setServiceCode(res?.serviceCode || '');
          setCardSequenceNumber(res.cardSequenceNumber || '');
          setResult(true);
          setIsScanning(false);
        }
      })
      .catch((e) => {
        setIsScanning(false);
        setError(e?.message);
        console.log(e);
        stopScanCard();
      });
  };
  const printCardValue = () => {
    let size = 18;
    let position = 1;
    openPrinter();
    cleanCache();
    // printTextLine('', size, position, false, false, false);
    // printTextLine('', size, position, false, false, false);
    printTextLine(`Card Number: ${cardNumber}`, size, position, false, false, false);
    printTextLine(`Holder: ${cardHolderName}`, size, position, false, false, false);
    printTextLine(`Expire Date: ${cardExpireDate}`, size, position, false, false, false);
    printTextLine(`App Label: ${appLabel}`, size, position, false, false, false);
    printTextLine('', size, position, false, false, false);
    printTextLine('', size, position, false, false, false);
    printTextLine('', size, position, false, false, false);
    printTextLine('', size, position, false, false, false);
    beginPrint();
    closePrinter();
  };
  const printLine = (content = "Không có kết quả", position = 1, size = 25) => {
    
    openPrinter();
    cleanCache();
    // printTextLine('', size, position, false, false, false);
    printTextLine(content, size, position, false, false, false);
    // printTextLine('content');
    beginPrint();
    closePrinter();
  };

  const printResult = () => {
    console.log('Đang in');
    if(result){
      printCardValue();
    }
    else{
      printLine()
    }
  }

  return (
    <View style={styles.container}>
      {result && (
        <>
          <Text>Card Number: {cardNumber}</Text>
          <Text>Holder Name: {cardHolderName}</Text>
          <Text>Expire Date: {cardExpireDate}</Text>
          <Text>Issuer Country: {issuerCountry}</Text>
          <Text>App Lable: {appLabel}</Text>
          <Text>Service Code: {serviceCode}</Text>
          <Text>Sequence Number: {cardSequenceNumber}</Text>
        </>
      )}
      {error && <Text style={{ color: 'red' }}>{error}</Text>}
      <TouchableOpacity style={styles.btn} onPress={handleScan}>
        <Text style={{ fontSize: 18, color: 'white', textAlign: 'center' }}>
          {isScanning ? 'Scanning' : 'Scan'}
        </Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.btn} onPress={() => printResult()}>
        <Text style={{ fontSize: 18, color: 'white', textAlign: 'center' }}>
          Print
        </Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  btn: {
    width: '100%',
    margin: 20,
    backgroundColor: 'black',
    padding: 10,
  },
});