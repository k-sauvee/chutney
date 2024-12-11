import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Hit } from '@core/model/search/hit.model';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
    private searchUri = '/api/search';

    constructor(private httpClient: HttpClient) {
    }

    search(keyword: string): Observable<Hit[]> {
        return this.httpClient.get<Hit[]>(environment.backend + `${this.searchUri}?keyword=${keyword}`);
    }
}
