import React, { Component } from 'react';
import { AppRegistry, StyleSheet, Text, View, Button } from 'react-native';
import DatecsPrinter from 'react-native-datecs-printer';

export default class DatecsPrinterExample extends Component {

	constructor(props) {
		super(props);

		this.connect    = this.connect.bind(this);
		this.print      = this.print.bind(this);
		this.disconnect = this.disconnect.bind(this);
		this.getStatus  = this.getStatus.bind(this);
	}

	connect(){
		DatecsPrinter.connect()
		.then((res) => {
			// return CONNECTED
			console.log(res);
		})
		.catch((err) => {
			console.log(err)
		})
	}

	print(text){
		DatecsPrinter.printText(text)
		.then((res) => {
			// return PRINTED
			console.log(res);
		})
		.catch((err) => {
			console.log(err)
		})
	}

	printSelfTest(){
		// It need to be tested, I'm almost sure the promise will result in an error
		DatecsPrinter.printSelfTest();
	}

	getStatus(){
		DatecsPrinter.getStatus()
		.then((res) => {
			// If everything is OK, it'll return 0
			// you can use this before printing to make sure that nothing wrong happens
			console.log(res);
		})
		.catch((err) => {
			console.log(err)
		})
	}

	disconnect(){
		DatecsPrinter.disconnect()
		.then((res) => {
			// return DISCONNECTED
			console.log(res);
		})
		.catch((err) => {
			console.log(err)
		})
	}

	componentDidMount(){
	}

	render() {
		return (
			<View style={styles.container}>

				<View style={{marginBottom: 15}}>
					<Button title="Connect" onPress={() => this.connect()} />
				</View>

				<View style={{marginBottom: 15}}>
					<Button title="Get Print Status" onPress={() => this.getStatus()} />
				</View>

				<View style={{marginBottom: 15}}>
					<Button title="Print Self Test" onPress={() => this.printSelfTest()} />
				</View>

				<View style={{marginBottom: 15}}>
					<Button title="Print Custom Text" onPress={() => this.print('{reset}{center}Test RN Datecs Printer')} />
				</View>

				<View style={{marginBottom: 15}}>
					<Button title="Disconnect" onPress={() => this.disconnect()} />
				</View>
			</View>
		);
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		flexDirection: 'column',
		justifyContent: 'center',
		alignItems: 'stretch',
		padding: 15,
		backgroundColor: '#F5FCFF',
	},
});

AppRegistry.registerComponent('DatecsPrinterExample', () => DatecsPrinterExample);
