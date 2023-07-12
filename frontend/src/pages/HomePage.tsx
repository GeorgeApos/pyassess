import mainImage from '../assets/RAISE_First_image.png';
import React, {useEffect, useState} from "react";
import axios from "axios";
import {Loading} from "../components/Loading";
import {projectResponseData} from "../interfaces/interfaces";
import './HomePage.css'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';


export const HomePage = () => {
    const [inputValue, setInputValue] = useState('');
    const [loading, setLoading] = useState(false);
    const [responseData, setResponseData] = useState<projectResponseData>();
    const [actionsText, setActionsText] = useState('');
    const [gitlabCiButtonPressed, setGitlabCiButtonPressed] = useState(false);

    const localhost = import.meta.env.VITE_REACT_APP_LOCALHOST;


    const handleGitlabCiButtonClick = () => {
        setGitlabCiButtonPressed(true);
        setActionsText("script:\n" +
            "  - echo \"Build stage...\"\n" +
            "  - git_url=$(git config --get remote.origin.url)\n" +
            "  - branch=$(git rev-parse --abbrev-ref HEAD)\n" +
            "  -- curl --request POST \"http://localhost:8080/projects/?git_url=${git_url}&branch=${branch}\"\n");
    }

    const handleShowButtonClick = () => {
        axios.get(`http://${localhost}:8080/projects/?gitUrl=${inputValue}`,
            {headers: {'Content-Type': 'application/json',
                    'Access-Control-Allow-Origin': '*'}})
            .then((response) => {
                setResponseData(response.data);
                console.log(response.data);
                response.data.projectAnalysis.reverse();
            })
            .catch((error) => {
                console.error(error);
            });
    }

    const handleStartButtonClick = () => {
        setLoading(true);
        axios.post(`http://${localhost}:8080/projects/?gitUrl=${inputValue}`,
            {headers: {'Content-Type': 'application/json',
                    'Access-Control-Allow-Origin': '*'}})
            .then((response) => {
                setLoading(false);
                setResponseData(response.data);
            })
            .catch((error) => {
                console.error(error);
                setLoading(false);
            });
    }

    const handleInputChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setInputValue(event.target.value);
    }

    const data = responseData?.projectAnalysis.map(analysis => ({
        sha: analysis.sha,
        totalStmts: analysis.totalStmts,
        dependenciesCounter: analysis.dependenciesCounter,
        totalMiss: analysis.totalMiss,
        totalCoverage: analysis.totalCoverage,
    }));

    return (
        <>
            {responseData ? (
                <>
                    <div className="project-data-container">
                        <h2 className="section-title">Project Data</h2>
                        <table className="data-table">
                            <thead className="table-head">
                            <tr className="table-row">
                                <th className="table-header">Git URL</th>
                                <th className="table-header">Owner</th>
                                <th className="table-header">Name</th>
                                <th className="table-header">SHAs</th>
                            </tr>
                            </thead>
                            <tbody className="table-body">
                            <tr className="table-row">
                                <td className="table-data">{responseData.gitUrl}</td>
                                <td className="table-data">{responseData.owner}</td>
                                <td className="table-data">{responseData.name}</td>
                                <td className="table-data">{(
                                    responseData.sha.map((sha) => (
                                        <div key={sha}> - {sha}</div>
                                    ))
                                )}</td>
                            </tr>
                            </tbody>
                        </table>

                        <div className="chart-container">
                            <div className="chart">
                                <h2 className="section-title">Total Statements</h2>
                                <LineChart width={600} height={300} data={data}
                                           margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                    <XAxis dataKey="sha"/>
                                    <YAxis/>
                                    <CartesianGrid strokeDasharray="3 3"/>
                                    <Tooltip/>
                                    <Legend/>
                                    <Line type="monotone" dataKey="totalStmts" stroke="#8884d8"/>
                                </LineChart>
                            </div>
                            <div className="chart">

                                <h2 className="section-title">Dependencies</h2>
                                <LineChart width={600} height={300} data={data}
                                           margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                    <XAxis dataKey="sha"/>
                                    <YAxis/>
                                    <CartesianGrid strokeDasharray="3 3"/>
                                    <Tooltip/>
                                    <Legend/>
                                    <Line type="monotone" dataKey="dependenciesCounter" stroke="#8884d8"/>
                                </LineChart>
                            </div>
                            <div className="chart">

                                <h2>Total Miss Chart</h2>
                                <LineChart width={600} height={300} data={data}>
                                    <CartesianGrid strokeDasharray="3 3"/>
                                    <XAxis dataKey="sha"/>
                                    <YAxis/>
                                    <Tooltip/>
                                    <Legend/>
                                    <Line type="monotone" dataKey="totalMiss" stroke="#8884d8" activeDot={{r: 8}}/>
                                </LineChart>
                            </div>
                            <div className="chart">

                                <h2>Total Coverage Chart</h2>
                                <LineChart width={600} height={300} data={data}>
                                    <CartesianGrid strokeDasharray="3 3"/>
                                    <XAxis dataKey="sha"/>
                                    <YAxis/>
                                    <Tooltip/>
                                    <Legend/>
                                    <Line type="monotone" dataKey="totalCoverage" stroke="#82ca9d" activeDot={{r: 8}}/>
                                </LineChart>
                            </div>
                        </div>

                        <button className="back-button" type="submit" style={{marginTop: 20}}
                                onClick={() => setResponseData(undefined)}>Back
                        </button>
                    </div>
                </>
            ) : gitlabCiButtonPressed ? (
                <>
                <textarea
                    value={actionsText}
                    readOnly
                    rows={Math.max(actionsText.split('\n').length, 10)}
                    cols={Math.max(actionsText.length / 100, 100)}
                />                    <br />
                    <button className="back-button" type="submit" style={{marginTop: 20}}
                            onClick={() => setGitlabCiButtonPressed(false)}>Back
                    </button>
                </>
            ) : (
                <div>
                    <img src={mainImage} width={300} />
                    <h2>Provide a Github URL</h2>
                    <p>Enter a Github URL to start the analysis of a project.</p>
                    <textarea value={inputValue} onChange={handleInputChange} />
                    <br />
                    {loading ? (
                        <Loading />
                    ) : (
                        <div>
                            <button
                                type={"submit"}
                                style={{ marginTop: 20, marginRight: 20 }}
                                onClick={handleStartButtonClick}
                            >
                                Start Analysis
                            </button>
                            <button
                                type={"submit"}
                                style={{ marginTop: 20, marginRight: 20 }}
                                onClick={handleGitlabCiButtonClick}
                            >
                                Gitlab CI
                            </button>
                            <button
                                type={"submit"}
                                style={{ marginTop: 20 }}
                                onClick={handleShowButtonClick}
                            >
                                Show data
                            </button>
                        </div>
                    )}
                </div>
            )}
        </>
    );
}