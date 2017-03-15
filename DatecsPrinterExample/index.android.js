import React, { Component } from 'react';
import { AppRegistry, StyleSheet, Text, View } from 'react-native';
import DatecsPrinter from 'react-native-datecs-printer';

export default class DatecsPrinterExample extends Component {

	constructor(props) {
		super(props);
	}

	componentDidMount(){
		DatecsPrinter.show();
	}

	render() {
		return (
			<View style={styles.container}>
				<Text style={styles.welcome}>
					fon
				</Text>
			</View>
		);
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: '#F5FCFF',
	},
	welcome: {
		fontSize: 20,
		textAlign: 'center',
		margin: 10,
	},
	instructions: {
		textAlign: 'center',
		color: '#333333',
		marginBottom: 5,
	},
});

AppRegistry.registerComponent('DatecsPrinterExample', () => DatecsPrinterExample);
