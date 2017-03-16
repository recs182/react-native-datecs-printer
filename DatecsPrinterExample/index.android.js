import React, { Component } from 'react';
import { AppRegistry, StyleSheet, Text, View, Button } from 'react-native';
import DatecsPrinter from 'react-native-datecs-printer';

export default class DatecsPrinterExample extends Component {

	constructor(props) {
		super(props);

		this.connect = this.connect.bind(this);
		this.print = this.print.bind(this);
		this.disconnect = this.disconnect.bind(this);
	}

	connect(){
		DatecsPrinter.connect()
		.then((res) => {
			console.log(res);
		})
		.catch((err) => {
			console.log(err)
		})
	}

	print(text){
		DatecsPrinter.printText(text);
	}

	disconnect(){
		DatecsPrinter.disconnect()
		.then((res) => {
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
				<Button title="Connect" onPress={() => this.connect()} />


				<Button title="Print" onPress={() => this.print('{reset}{center}teste')} />


				<Button title="Disconnect" onPress={() => this.disconnect()} />
			</View>
		);
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		flexDirection: 'column',
		justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: '#F5FCFF',
	},
});

AppRegistry.registerComponent('DatecsPrinterExample', () => DatecsPrinterExample);
