export type projectResponseData = {
    id: number;
    gitUrl: string;
    owner: string;
    name: string;
    directory: string;
    sha: string[];
    projectAnalysis: projectAnalysisData[];
}
export type projectAnalysisData= {
    id: number;
    gitUrl: string;
    sha : string;
    owner: string;
    name: string;
    directory: string;
    dependencies: string[];
    dependenciesCounter: number;
    files: projectFileResponseData[];
    totalCoverage: number;
    totalMiss: number;
    totalStmts: number;

}

export type projectFileResponseData = {
    id: number;
    first_file: string;
    name: string;
    stmts: number;
    miss: number;
    coverage: number;
    comments: commentResponseData[];
    similarity: Similarity;
    rating: number;
    previousRating: number;
    project: projectResponseData;
    projectName: string;
}

export type commentResponseData = {
    comment: string;
}

interface Similarity {
    [fileName: string]: number;
}
